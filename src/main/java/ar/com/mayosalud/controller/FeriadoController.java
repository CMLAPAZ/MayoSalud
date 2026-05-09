package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.TipoFeriado;
import ar.com.mayosalud.service.FeriadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/** Gestión de feriados — solo accesible para ADMIN. */
@Controller
@RequestMapping("/feriados")
@RequiredArgsConstructor
public class FeriadoController {

    private final FeriadoService feriadoService;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false, defaultValue = "0") int anio) {
        if (anio == 0) anio = LocalDate.now().getYear();
        model.addAttribute("feriados", feriadoService.listarAnio(anio));
        model.addAttribute("anio", anio);
        model.addAttribute("anioAnterior", anio - 1);
        model.addAttribute("anioSiguiente", anio + 1);
        return "feriado/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("feriado", new Feriado());
        model.addAttribute("tipos", TipoFeriado.values());
        return "feriado/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("feriado", feriadoService.buscarPorId(id));
        model.addAttribute("tipos", TipoFeriado.values());
        return "feriado/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Feriado feriado, BindingResult result,
                          Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoFeriado.values());
            return "feriado/form";
        }
        feriadoService.guardar(feriado);
        redirectAttrs.addFlashAttribute("exito", "Feriado guardado correctamente.");
        return "redirect:/feriados?anio=" + feriado.getFecha().getYear();
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @RequestParam(defaultValue = "0") int anio,
                           RedirectAttributes redirectAttrs) {
        feriadoService.eliminar(id);
        redirectAttrs.addFlashAttribute("exito", "Feriado eliminado.");
        return "redirect:/feriados?anio=" + (anio == 0 ? LocalDate.now().getYear() : anio);
    }
}
