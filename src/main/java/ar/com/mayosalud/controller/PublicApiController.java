package ar.com.mayosalud.controller;

import ar.com.mayosalud.dto.PublicHorarioMedicoResponse;
import ar.com.mayosalud.dto.PublicMedicoResponse;
import ar.com.mayosalud.dto.TurnosLibresResponse;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.repository.HorarioAtencionMedicoRepository;
import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** API publica de solo lectura para integracion con WordPress. */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicApiController {

    private final MedicoService medicoService;
    private final TurnoService turnoService;
    private final HorarioAtencionMedicoRepository horarioRepository;

    @GetMapping("/medicos")
    public List<PublicMedicoResponse> medicos() {
        return medicoService.listarActivos().stream()
                .map(this::toPublicMedico)
                .toList();
    }

    @GetMapping("/medicos/{medicoId}/horarios")
    public List<PublicHorarioMedicoResponse> horariosMedico(@PathVariable Long medicoId) {
        Medico medico = buscarMedicoActivo(medicoId);
        return horarioRepository.findByMedicoAndActivoTrueOrderByDiaSemanaAscHoraDesdeAsc(medico)
                .stream()
                .map(h -> new PublicHorarioMedicoResponse(
                        h.getDiaSemana().name(),
                        h.getHoraDesde().toString(),
                        h.getHoraHasta().toString(),
                        h.getDuracionBaseMinutos()
                ))
                .toList();
    }

    @GetMapping("/turnos/libres")
    public TurnosLibresResponse turnosLibres(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "30") Integer duracionMinutos) {

        Medico medico = buscarMedicoActivo(medicoId);
        return turnoService.calcularTurnosLibres(medico, fecha, duracionMinutos);
    }

    private PublicMedicoResponse toPublicMedico(Medico medico) {
        return new PublicMedicoResponse(
                medico.getId(),
                medico.getNombreCompleto(),
                medico.getEspecialidad().name()
        );
    }

    private Medico buscarMedicoActivo(Long medicoId) {
        Medico medico = medicoService.buscarPorId(medicoId);
        if (!medico.isActivo()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medico no disponible");
        }
        return medico;
    }
}
