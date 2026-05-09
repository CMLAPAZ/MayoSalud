package ar.com.mayosalud.config;

import ar.com.mayosalud.service.LoginAttemptService;
import ar.com.mayosalud.service.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
                // Solo ADMIN
                .requestMatchers("/usuarios/**", "/feriados/**", "/auditoria/**").hasRole("ADMIN")
                // ADMIN + RECEPCION (MEDICO no gestiona médicos)
                .requestMatchers("/medicos/**").hasAnyRole("ADMIN", "RECEPCION")
                // ADMIN + RECEPCION: escritura sobre pacientes y turnos
                .requestMatchers(
                    "/pacientes/nuevo", "/pacientes/guardar",
                    "/pacientes/editar/**", "/pacientes/eliminar/**",
                    "/pacientes/consentimiento/**"
                ).hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers(
                    "/turnos/nuevo", "/turnos/guardar",
                    "/turnos/editar/**", "/turnos/eliminar/**",
                    "/turnos/estado/**"
                ).hasAnyRole("ADMIN", "RECEPCION")
                // Todo lo demás requiere autenticación (MEDICO puede ver agenda y ficha de paciente)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                .permitAll()
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
