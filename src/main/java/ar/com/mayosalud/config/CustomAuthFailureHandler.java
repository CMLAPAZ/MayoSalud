package ar.com.mayosalud.config;

import ar.com.mayosalud.service.AuditoriaService;
import ar.com.mayosalud.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/** Maneja fallos de login: incrementa el contador de intentos, bloquea si supera el límite y registra en auditoría. */
@Component
@RequiredArgsConstructor
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;
    private final AuditoriaService auditoriaService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");
        String ip = obtenerIp(request);

        loginAttemptService.loginFallido(username);

        if (loginAttemptService.estaBloqueado(username)) {
            auditoriaService.registrar("BLOQUEADO", "LOGIN", username,
                    "Usuario bloqueado por " + LoginAttemptService.MAX_INTENTOS + " intentos fallidos consecutivos",
                    username, ip);
            response.sendRedirect("/login?bloqueado");
        } else {
            int intentos = loginAttemptService.getIntentos(username);
            auditoriaService.registrar("LOGIN_FALLIDO", "LOGIN", username,
                    "Intento fallido " + intentos + " de " + LoginAttemptService.MAX_INTENTOS + " para usuario: " + username,
                    username, ip);
            response.sendRedirect("/login?error");
        }
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
