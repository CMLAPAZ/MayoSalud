package ar.com.mayosalud.config;

import ar.com.mayosalud.service.FeriadoService;
import ar.com.mayosalud.service.LoginAttemptService;
import ar.com.mayosalud.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Inicializa datos de arranque: admin por defecto y feriados precargados. */
@Component
@RequiredArgsConstructor
@Order(1)
public class UsuarioInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;
    private final FeriadoService feriadoService;
    private final LoginAttemptService loginAttemptService;

    @Override
    public void run(ApplicationArguments args) {
        usuarioService.inicializarAdmin();
        loginAttemptService.loginExitoso("admin"); // limpia bloqueo en memoria al arrancar
        feriadoService.inicializarSiVacio();
    }
}
