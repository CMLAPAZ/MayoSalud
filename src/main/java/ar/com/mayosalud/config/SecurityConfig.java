package ar.com.mayosalud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ar.com.mayosalud.service.LoginAttemptService;
import ar.com.mayosalud.service.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;


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

    @Value("${mayosalud.cors.allowed-origins:https://clinicamayolapaz.com.ar}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                // Páginas institucionales públicas
                .requestMatchers("/inicio", "/medicos", "/especialidades", "/nosotros", "/contacto").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/public/**").permitAll()

                // ABM completo para ADMIN
                .requestMatchers("/pacientes/**").hasRole("ADMIN")
                .requestMatchers("/medicos/**").hasRole("ADMIN")

                // Turnos: ENFERMERIA solo puede ver la agenda (GET /turnos/**),
                // pero no puede acceder a la edición (GET /turnos/editar/**).
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/turnos/editar/**").hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/turnos/**").hasAnyRole("ADMIN", "RECEPCION", "ENFERMERIA", "MEDICO")


.requestMatchers(org.springframework.http.HttpMethod.POST, "/turnos/eliminar/**").hasAnyRole("ADMIN", "RECEPCION")

                





                // Pacientes administrativo: solo ADMIN y RECEPCION (ABM)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/pacientes/**").hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/pacientes/**").hasRole("ADMIN")

                // Ficha clínica - GET: ADMIN, MEDICO y ENFERMERIA. RECEPCION bloqueada.
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/clinica/pacientes/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMERIA")
                // Signos vitales: responsabilidad de enfermería → ADMIN y ENFERMERIA.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/clinica/pacientes/ver/*/signos-vitales").hasAnyRole("ADMIN", "ENFERMERIA")
                // Evolución médica: diagnóstico médico → ADMIN y MEDICO.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/clinica/pacientes/ver/*/evolucion-medica").hasAnyRole("ADMIN", "MEDICO")
                // Estudios: prescripción médica → ADMIN y MEDICO.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/clinica/pacientes/ver/*/estudio").hasAnyRole("ADMIN", "MEDICO")
                // Resto de /clinica/pacientes/**: ADMIN, MEDICO y ENFERMERIA (fallback para rutas futuras).
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/clinica/pacientes/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMERIA")

                // Administración extra solo ADMIN
                .requestMatchers("/horarios-medicos/**").hasRole("ADMIN")
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
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                    "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; " +
                    "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
                    "img-src 'self' data:; " +
                    "frame-ancestors 'self' https://clinicamayolapaz.com.ar"
                ))
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(true)
                    .maxAgeInSeconds(31536000)
                )
                .contentTypeOptions(contentType -> {})
                .referrerPolicy(referrer -> referrer
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(parseCsv(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    private List<String> parseCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }
}
