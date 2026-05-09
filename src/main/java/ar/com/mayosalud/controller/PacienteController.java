package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.ObraSocial;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

/** Gestiona las vistas y acciones del módulo de pacientes: listado, alta/edición, detalle y baja lógica. */
@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final TurnoService turnoService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("pacientes", pacienteService.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("pacientes", pacienteService.listarActivos());
        }
        return "paciente/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("obrasSociales", ObraSocial.values());
        return "paciente/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", pacienteService.buscarPorId(id));
        model.addAttribute("obrasSociales", ObraSocial.values());
        return "paciente/form";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        model.addAttribute("paciente", paciente);
        model.addAttribute("turnos", turnoService.listarPorPaciente(paciente));
        return "paciente/detalle";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Paciente paciente, BindingResult result,
                          @RequestParam(defaultValue = "false") boolean consentimiento,
                          Model model, RedirectAttributes redirectAttrs) {
        boolean esNuevo = paciente.getId() == null;

        if (esNuevo && !consentimiento) {
            model.addAttribute("errorConsentimiento", "Debe aceptar el consentimiento informado para registrar el paciente.");
            model.addAttribute("obrasSociales", ObraSocial.values());
            return "paciente/form";
        }
        if (result.hasErrors()) {
            model.addAttribute("obrasSociales", ObraSocial.values());
            return "paciente/form";
        }
        try {
            if (esNuevo) {
                paciente.setConsentimientoFecha(LocalDateTime.now());
            }
            pacienteService.guardar(paciente);
            redirectAttrs.addFlashAttribute("exito", "Paciente guardado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("obrasSociales", ObraSocial.values());
            return "paciente/form";
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/baja/{id}")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        pacienteService.darDeBaja(id);
        redirectAttrs.addFlashAttribute("exito", "Paciente dado de baja correctamente.");
        return "redirect:/pacientes";
    }
}
