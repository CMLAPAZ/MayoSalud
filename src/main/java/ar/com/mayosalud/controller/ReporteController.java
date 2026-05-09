package ar.com.mayosalud.controller;

import ar.com.mayosalud.service.ReporteService;
import ar.com.mayosalud.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    /** Agenda diaria completa: hora, paciente, médico, especialidad, estado y motivo. */
    @GetMapping("/agenda-pdf")
    public ResponseEntity<byte[]> agendaDiaria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        byte[] pdf = reporteService.generarAgendaDiaria(fecha, turnoService.listarPorFecha(fecha));
        return pdfResponse(pdf, "agenda-" + fecha + ".pdf");
    }

    /** Lista simplificada de pacientes del día: nombre, DNI, cobertura y médico. */
    @GetMapping("/pacientes-pdf")
    public ResponseEntity<byte[]> pacientesDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        byte[] pdf = reporteService.generarPacientesDia(fecha, turnoService.listarPorFecha(fecha));
        return pdfResponse(pdf, "pacientes-" + fecha + ".pdf");
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
