package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Gestiona la agenda diaria de turnos: navegación por fecha, cambio de estado y CRUD. */
@Controller
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;

    private static final DateTimeFormatter FMT_DIA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));

    @GetMapping
    public String agenda(Model model,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();
        List<Turno> turnos = turnoService.listarPorFecha(fecha);

        long cntPendiente  = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.PENDIENTE).count();
        long cntConfirmado = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.CONFIRMADO).count();
        long cntAtendido   = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.ATENDIDO).count();
        long cntCancelado  = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.CANCELADO
                                                      || t.getEstado() == EstadoTurno.AUSENTE).count();

        model.addAttribute("turnos", turnos);
        model.addAttribute("fecha", fecha);
        model.addAttribute("fechaFormateada", fecha.format(FMT_DIA));
        model.addAttribute("esHoy", fecha.equals(LocalDate.now()));
        model.addAttribute("fechaAnterior", fecha.minusDays(1));
        model.addAttribute("fechaSiguiente", fecha.plusDays(1));
        model.addAttribute("estadosTurno", EstadoTurno.values());
        model.addAttribute("cntPendiente",  cntPendiente);
        model.addAttribute("cntConfirmado", cntConfirmado);
        model.addAttribute("cntAtendido",   cntAtendido);
        model.addAttribute("cntCancelado",  cntCancelado);
        turnoService.getFeriado(fecha).ifPresent(f ->
                model.addAttribute("feriadoDelDia", f));
        return "turno/agenda";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model,
                        @RequestParam(required = false) Long pacienteId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Turno turno = new Turno();
        if (pacienteId != null) turno.setPaciente(pacienteService.buscarPorId(pacienteId));
        if (fecha != null) {
            turno.setFecha(fecha);
            turnoService.getFeriado(fecha).ifPresent(f ->
                    model.addAttribute("feriadoAviso", f));
        }
        model.addAttribute("turno", turno);
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("pacientes", pacienteService.listarActivos());
        model.addAttribute("estados", EstadoTurno.values());
        return "turno/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("turno", turnoService.buscarPorId(id));
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("pacientes", pacienteService.listarActivos());
        model.addAttribute("estados", EstadoTurno.values());
        return "turno/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Turno turno, BindingResult result,
                          Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("medicos", medicoService.listarActivos());
            model.addAttribute("pacientes", pacienteService.listarActivos());
            model.addAttribute("estados", EstadoTurno.values());
            return "turno/form";
        }
        try {
            turnoService.guardar(turno);
            redirectAttrs.addFlashAttribute("exito", "Turno guardado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicos", medicoService.listarActivos());
            model.addAttribute("pacientes", pacienteService.listarActivos());
            model.addAttribute("estados", EstadoTurno.values());
            return "turno/form";
        }
        return "redirect:/turnos?fecha=" + turno.getFecha();
    }

    @PostMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoTurno estado,
                                @RequestParam String fechaRedirect,
                                RedirectAttributes redirectAttrs) {
        turnoService.cambiarEstado(id, estado);
        redirectAttrs.addFlashAttribute("exito", "Estado actualizado.");
        return "redirect:/turnos?fecha=" + fechaRedirect;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @RequestParam String fechaRedirect,
                           RedirectAttributes redirectAttrs) {
        turnoService.eliminar(id);
        redirectAttrs.addFlashAttribute("exito", "Turno eliminado.");
        return "redirect:/turnos?fecha=" + fechaRedirect;
    }
}
