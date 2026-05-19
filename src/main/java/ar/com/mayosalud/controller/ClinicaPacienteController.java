package ar.com.mayosalud.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.EstadoEstudio;
import ar.com.mayosalud.entity.EventoClinico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.service.EventoClinicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import ar.com.mayosalud.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/clinica/pacientes")
@RequiredArgsConstructor
public class ClinicaPacienteController {

    private final PacienteService pacienteService;
    private final TurnoService turnoService;
    private final EventoClinicoService eventoClinicoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String buscar, Authentication authentication) {
        List<Paciente> pacientes;
        if (buscar != null && !buscar.isBlank()) {
            pacientes = pacienteService.buscar(buscar);
            model.addAttribute("buscar", buscar);
        } else {
            pacientes = pacienteService.listarActivos();
        }
        model.addAttribute("pacientes", pacientes);
        return "clinica/paciente/lista";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, Authentication authentication) {
        Paciente paciente = pacienteService.buscarPorId(id);
        validarAccesoPaciente(authentication, paciente);
        List<EventoClinico> eventos = eventoClinicoService.listarUltimos20PorPaciente(paciente);
        model.addAttribute("paciente", paciente);
        model.addAttribute("turnos", turnoService.listarPorPaciente(paciente));
        model.addAttribute("eventos", eventos);
        model.addAttribute("estadosEstudio", EstadoEstudio.values());
        return "clinica/paciente/detalle";
    }

    @PostMapping("/ver/{id}/signos-vitales")
    public String guardarSignosVitales(
            @PathVariable Long id,
            @RequestParam(required = false) Double temperatura,
            @RequestParam(required = false) Integer presionSistolica,
            @RequestParam(required = false) Integer presionDiastolica,
            @RequestParam(required = false) Integer pulsoFrecuencia,
            @RequestParam(required = false) Double saturacionOxigeno,
            @RequestParam(required = false) Double peso,
            @RequestParam(required = false) Double talla,
            @RequestParam(required = false) String observacionesEspecificas,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            validarAccesoPaciente(authentication, pacienteService.buscarPorId(id));
            eventoClinicoService.guardarSignosVitales(id, temperatura, presionSistolica,
                    presionDiastolica, pulsoFrecuencia, saturacionOxigeno, peso, talla,
                    observacionesEspecificas);
            redirectAttributes.addFlashAttribute("exito", "Signos vitales registrados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar los signos vitales. Revisá los datos e intentá nuevamente.");
        }
        return "redirect:/clinica/pacientes/ver/" + id;
    }

    @PostMapping("/ver/{id}/evolucion-medica")
    public String guardarEvolucionMedica(
            @PathVariable Long id,
            @RequestParam(required = false) String motivoConsulta,
            @RequestParam(required = false) String evolucionTexto,
            @RequestParam(required = false) String examenFisico,
            @RequestParam(required = false) String impresionDiagnostica,
            @RequestParam(required = false) String conducta,
            @RequestParam(required = false) String indicaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            validarAccesoPaciente(authentication, pacienteService.buscarPorId(id));
            eventoClinicoService.guardarEvolucionMedica(id, motivoConsulta, evolucionTexto,
                    examenFisico, impresionDiagnostica, conducta, indicaciones);
            redirectAttributes.addFlashAttribute("exito", "Evolución médica registrada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar la evolución médica. Revisá los datos e intentá nuevamente.");
        }
        return "redirect:/clinica/pacientes/ver/" + id;
    }

    @PostMapping("/ver/{id}/estudio")
    public String guardarEstudio(
            @PathVariable Long id,
            @RequestParam(required = false) String tipoEstudio,
            @RequestParam(required = false) String nombreEstudio,
            @RequestParam(required = false) String indicacion,
            @RequestParam(required = false) EstadoEstudio estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSolicitud,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaRealizacion,
            @RequestParam(required = false) String resultadoTexto,
            @RequestParam(required = false) String resultadoUrl,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            validarAccesoPaciente(authentication, pacienteService.buscarPorId(id));
            if (estado == null) estado = EstadoEstudio.SOLICITADO;
            eventoClinicoService.guardarEstudio(id, tipoEstudio, nombreEstudio, indicacion,
                    estado, fechaSolicitud, fechaRealizacion, resultadoTexto, resultadoUrl);
            redirectAttributes.addFlashAttribute("exito", "Estudio registrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar el estudio. Revisá los datos e intentá nuevamente.");
        }
        return "redirect:/clinica/pacientes/ver/" + id;
    }

    private void validarAccesoPaciente(Authentication authentication, Paciente paciente) {
        // El médico puede consultar la ficha de cualquier paciente de la institución
        // (atención directa, interconsulta, guardia, reemplazo, continuidad asistencial).
        // Auditoría de acceso por interconsulta: etapa futura.
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
}
