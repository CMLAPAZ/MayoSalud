package ar.com.mayosalud.config;

import ar.com.mayosalud.service.FeriadoService;
import ar.com.mayosalud.service.LoginAttemptService;
import ar.com.mayosalud.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Inicializa datos de arranque: admin por defecto y feriados precargados. */
@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class UsuarioInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;
    private final FeriadoService feriadoService;
    private final LoginAttemptService loginAttemptService;
    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        limpiarFechasZero();
        usuarioService.inicializarAdmin();
        loginAttemptService.loginExitoso("admin");
        feriadoService.inicializarSiVacio();
    }

    /** Convierte '0000-00-00 00:00:00' a valores seguros para evitar Zero date prohibited. */
    private void limpiarFechasZero() {
        try {
            int n1 = jdbc.update(
                "UPDATE usuarios SET fecha_creacion = NOW() WHERE fecha_creacion = '0000-00-00 00:00:00'");
            int n2 = jdbc.update(
                "UPDATE usuarios SET ultimo_acceso = NULL WHERE ultimo_acceso = '0000-00-00 00:00:00'");
            int n3 = jdbc.update(
                "UPDATE auditoria_log SET fecha_hora = NOW() WHERE fecha_hora = '0000-00-00 00:00:00'");
            int n4 = jdbc.update(
                "UPDATE pacientes SET consentimiento_fecha = NULL WHERE consentimiento_fecha = '0000-00-00 00:00:00'");
            if (n1 + n2 + n3 + n4 > 0) {
                log.warn("Fechas cero corregidas — usuarios.fecha_creacion:{} ultimo_acceso:{} auditoria:{} pacientes:{}",
                        n1, n2, n3, n4);
            }
        } catch (Exception e) {
            log.warn("No se pudo limpiar fechas cero (tabla puede no existir aún): {}", e.getMessage());
        }
    }
}
