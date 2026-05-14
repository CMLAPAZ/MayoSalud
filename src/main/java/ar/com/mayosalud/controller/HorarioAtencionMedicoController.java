package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.DiaAtencion;
import ar.com.mayosalud.entity.HorarioAtencionMedico;
import ar.com.mayosalud.entity.TurnoDuracion;
import ar.com.mayosalud.service.HorarioAtencionMedicoService;
import ar.com.mayosalud.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioAtencionMedicoController {

    private final HorarioAtencionMedicoService horarioService;
    private final MedicoService medicoService;

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("medicos", medicoService.listarActivos());
        return "horario/lista";
    }

    @GetMapping("/medico/{medicoId}")
    public String listaPorMedico(@PathVariable Long medicoId, Model model) {
        var medico = medicoService.buscarPorId(medicoId);
        model.addAttribute("medico", medico);
        model.addAttribute("horarios", horarioService.listarPorMedico(medico));
        model.addAttribute("diasEspanol", DiaAtencion.NOMBRES);
        return "horario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long medicoId, Model model) {
        var horario = new HorarioAtencionMedico();
        horario.setDuracionBaseMinutos(30);
        horario.setActivo(true);
        if (medicoId != null) {
            horario.setMedico(medicoService.buscarPorId(medicoId));
        }
        model.addAttribute("horario", horario);
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("diasEspanol", DiaAtencion.NOMBRES);
        model.addAttribute("duracionesPermitidas", TurnoDuracion.PERMITIDAS);
        return "horario/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("horario", horarioService.buscarPorId(id));
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("diasEspanol", DiaAtencion.NOMBRES);
        model.addAttribute("duracionesPermitidas", TurnoDuracion.PERMITIDAS);
        return "horario/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute HorarioAtencionMedico horario,
                          BindingResult result, Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("medicos", medicoService.listarActivos());
            model.addAttribute("diasEspanol", DiaAtencion.NOMBRES);
            model.addAttribute("duracionesPermitidas", TurnoDuracion.PERMITIDAS);
            return "horario/form";
        }
        try {
            horarioService.guardar(horario);
            Long medicoId = horario.getMedico() != null ? horario.getMedico().getId() : null;
            redirectAttrs.addFlashAttribute("exito", "Horario guardado correctamente.");
            return "redirect:/horarios/medico/" + medicoId;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicos", medicoService.listarActivos());
            model.addAttribute("diasEspanol", DiaAtencion.NOMBRES);
            model.addAttribute("duracionesPermitidas", TurnoDuracion.PERMITIDAS);
            return "horario/form";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, @RequestParam Long medicoId,
                           RedirectAttributes redirectAttrs) {
        horarioService.eliminar(id);
        redirectAttrs.addFlashAttribute("exito", "Horario eliminado.");
        return "redirect:/horarios/medico/" + medicoId;
    }
}
