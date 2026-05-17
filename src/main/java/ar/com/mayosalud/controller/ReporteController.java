package ar.com.mayosalud.controller;

import ar.com.mayosalud.service.ReporteService;
import ar.com.mayosalud.service.TurnoService;
import ar.com.mayosalud.service.UsuarioService;
import ar.com.mayosalud.entity.Medico;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

/** Endpoints de reportes PDF — accesibles para todos los roles autenticados. */
@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final TurnoService turnoService;
    private final ReporteService reporteService;
    private final UsuarioService usuarioService;

    /** Agenda diaria completa: hora, paciente, médico, especialidad, estado y motivo. */
    @GetMapping("/agenda-pdf")
    public ResponseEntity<byte[]> agendaDiaria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Medico medicoActual = medicoActualSiRolMedico(authentication);
        var turnos = esRolMedico(authentication)
                ? turnoService.listarPorMedicoYFecha(medicoActual, fecha)
                : turnoService.listarPorFecha(fecha);
        byte[] pdf = reporteService.generarAgendaDiaria(fecha, turnos);
        return pdfResponse(pdf, "agenda-" + fecha + ".pdf");
    }

    /** Lista simplificada de pacientes del día: nombre, DNI, cobertura y médico. */
    @GetMapping("/pacientes-pdf")
    public ResponseEntity<byte[]> pacientesDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Medico medicoActual = medicoActualSiRolMedico(authentication);
        var turnos = esRolMedico(authentication)
                ? turnoService.listarPorMedicoYFecha(medicoActual, fecha)
                : turnoService.listarPorFecha(fecha);
        byte[] pdf = reporteService.generarPacientesDia(fecha, turnos);
        return pdfResponse(pdf, "pacientes-" + fecha + ".pdf");
    }

    private Medico medicoActualSiRolMedico(Authentication authentication) {
        if (!esRolMedico(authentication)) {
            return null;
        }
        return usuarioService.buscarPorUsername(authentication.getName())
                .map(u -> u.getMedico())
                .orElse(null);
    }

    private boolean esRolMedico(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MEDICO".equals(a.getAuthority()));
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
