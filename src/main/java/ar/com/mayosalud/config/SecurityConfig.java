package ar.com.mayosalud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import ar.com.mayosalud.service.LoginAttemptService;
import ar.com.mayosalud.service.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Configura autenticación desde BD, autorización por rol (ADMIN / RECEPCION / MEDICO),
 * sesión de 30 min y filtro anti fuerza bruta pre-login.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthFailureHandler authFailureHandler;
    private final CustomAuthSuccessHandler authSuccessHandler;
    private final LoginAttemptService loginAttemptService;
    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(
                (request, response, chain) -> {
                    HttpServletRequest req = (HttpServletRequest) request;
                    if ("POST".equalsIgnoreCase(req.getMethod())
                            && "/login".equals(req.getServletPath())) {
                        String username = req.getParameter("username");
                        if (username != null && loginAttemptService.estaBloqueado(username)) {
                            ((HttpServletResponse) response).sendRedirect("/login?bloqueado");
                            return;
                        }
                    }
                    chain.doFilter(request, response);
                },
                UsernamePasswordAuthenticationFilter.class
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/politica-privacidad").permitAll()

                // ABM completo para ADMIN
                .requestMatchers("/pacientes/**").hasRole("ADMIN")
                .requestMatchers("/medicos/**").hasRole("ADMIN")

                // Turnos: ENFERMERIA puede ver (GET), pero no cargar/editar/cancelar (POST)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/turnos/**").hasAnyRole("ADMIN", "RECEPCION", "ENFERMERIA")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/turnos/**").hasAnyRole("ADMIN", "RECEPCION")

                // Pacientes: ENFERMERIA solo ve (GET). ABM completo solo ADMIN.
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/pacientes/**").hasAnyRole("ADMIN", "RECEPCION", "ENFERMERIA")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/pacientes/**").hasRole("ADMIN")

                // Administración extra solo ADMIN
                .requestMatchers("/usuarios/**", "/feriados/**", "/auditoria/**").hasRole("ADMIN")

                // Resto: autenticación requerida
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                .permitAll()
            )
            // Evitar fallos de POST desde navegadores/dispositivos que no incluyan el token CSRF.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/turnos/guardar",
                    "/turnos/estado/**",
                    "/turnos/eliminar/**"
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?timeout")
                .maximumSessions(1)
            );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}

