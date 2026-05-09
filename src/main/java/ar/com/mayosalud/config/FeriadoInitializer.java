package ar.com.mayosalud.config;

import ar.com.mayosalud.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Carga los feriados predefinidos al iniciar la aplicación si la tabla está vacía. */
@Component
@RequiredArgsConstructor
public class FeriadoInitializer implements ApplicationRunner {

    private final FeriadoService feriadoService;

    @Override
    public void run(ApplicationArguments args) {
        feriadoService.inicializarSiVacio();
    }
}
