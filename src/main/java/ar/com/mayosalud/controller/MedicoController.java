package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.Especialidad;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestiona las vistas y acciones del módulo de médicos: listado, alta/edición y baja lógica. */
@Controller
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("medicos", medicoService.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("medicos", medicoService.listarActivos());
        }
        return "medico/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("especialidades", Especialidad.values());
        return "medico/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.buscarPorId(id));
        model.addAttribute("especialidades", Especialidad.values());
        return "medico/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Medico medico, BindingResult result,
                          Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("especialidades", Especialidad.values());
            return "medico/form";
        }
        try {
            medicoService.guardar(medico);
            redirectAttrs.addFlashAttribute("exito", "Médico guardado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("especialidades", Especialidad.values());
            return "medico/form";
        }
        return "redirect:/medicos";
    }

    @PostMapping("/baja/{id}")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        medicoService.darDeBaja(id);
        redirectAttrs.addFlashAttribute("exito", "Médico dado de baja correctamente.");
        return "redirect:/medicos";
    }
}
