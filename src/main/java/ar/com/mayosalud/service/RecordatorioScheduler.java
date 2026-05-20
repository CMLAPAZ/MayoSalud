package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Turno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Envía recordatorios automáticos por email cada mañana a los pacientes
 * con turno confirmado o pendiente para el día siguiente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecordatorioScheduler {

    private final TurnoService turnoService;
    private final EmailService emailService;
    private final AuditoriaService auditoriaService;

    @Value("${recordatorio.habilitado:true}")
    private boolean habilitado;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    /** Corre todos los días a las 08:00 hora Argentina. */
    @Scheduled(cron = "${recordatorio.cron:0 0 8 * * *}",
               zone  = "America/Argentina/Buenos_Aires")
    public void enviarRecordatorios() {
        if (!habilitado) {
            log.info("Recordatorios deshabilitados (recordatorio.habilitado=false)");
            auditoriaService.registrar("RECORDATORIOS_EMAIL", "Turno",
                    LocalDate.now().plusDays(1).toString(),
                    "Recordatorios deshabilitados (recordatorio.habilitado=false)",
                    "sistema", "scheduler");
            return;
        }
        if (mailUsername == null || mailUsername.isBlank()) {
            log.warn("Recordatorios omitidos: MAIL_USERNAME no configurado");
            auditoriaService.registrar("RECORDATORIOS_EMAIL", "Turno",
                    LocalDate.now().plusDays(1).toString(),
                    "Recordatorios omitidos: MAIL_USERNAME no configurado",
                    "sistema", "scheduler");
            return;
        }

        LocalDate manana = LocalDate.now().plusDays(1);
        List<Turno> turnos = turnoService.listarPorFecha(manana).stream()
                .filter(t -> t.getEstado() == EstadoTurno.PENDIENTE
                          || t.getEstado() == EstadoTurno.CONFIRMADO)
                .toList();

        if (turnos.isEmpty()) {
            String sinTurnos = "Recordatorios " + manana + ": sin turnos para recordar";
            log.info(sinTurnos);
            auditoriaService.registrar("RECORDATORIOS_EMAIL", "Turno", manana.toString(),
                    sinTurnos, "sistema", "scheduler");
            return;
        }

        int enviados = 0;
        int sinEmail = 0;
        int errores  = 0;

        for (Turno t : turnos) {
            String email = t.getPaciente().getEmail();
            if (email == null || email.isBlank()) {
                sinEmail++;
                continue;
            }
            boolean ok = emailService.enviarRecordatorio(t);
            if (ok) enviados++;
            else    errores++;
        }

        String resumen = String.format(
                "Recordatorios %s: %d enviados, %d sin email, %d errores (de %d turnos)",
                manana, enviados, sinEmail, errores, turnos.size());
        log.info(resumen);
        auditoriaService.registrar("RECORDATORIOS_EMAIL", "Turno", manana.toString(),
                resumen, "sistema", "scheduler");
    }
}
