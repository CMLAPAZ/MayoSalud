package ar.com.mayosalud.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Inyecta el URI del request en el modelo de todas las vistas.
 * Necesario porque Thymeleaf 3.1 bloquea el acceso directo a #httpServletRequest.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
