package ar.com.mayosalud.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.com.mayosalud.dto.TurnosLibresResponse;
import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.HorarioAtencionMedico;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.repository.HorarioAtencionMedicoRepository;
import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestiona la agenda de turnos: vista diaria, semanal, cambio de estado y CRUD. */
@Controller
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final HorarioAtencionMedicoRepository horarioRepository;

    private static final DateTimeFormatter FMT_DIA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    private static final DateTimeFormatter FMT_MES_ANIO =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    private static final DateTimeFormatter FMT_DIA_MES =
            DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "AR"));
    private static final String[] ABREV_DIA = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

    // ── Vista diaria ─────────────────────────────────────────────────────────

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
        turnoService.getFeriado(fecha).ifPresent(f -> model.addAttribute("feriadoDelDia", f));
        return "turno/agenda";
    }

    // ── Vista semanal ─────────────────────────────────────────────────────────

    @GetMapping("/semana")
    public String semana(Model model,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();

        LocalDate lunes  = fecha.with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);

        // Turnos agrupados por día (orden lunes → domingo)
        List<Turno> todos = turnoService.listarEntreFechas(lunes, domingo);
        Map<LocalDate, List<Turno>> turnosPorDia = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) turnosPorDia.put(lunes.plusDays(i), new ArrayList<>());
        todos.forEach(t -> turnosPorDia.get(t.getFecha()).add(t));

        // Feriados de la semana indexados por fecha
        Map<LocalDate, Feriado> feriadosPorDia = turnoService.getFeriadosSemana(lunes, domingo);

        // Etiquetas de día pre-formateadas para evitar problemas de locale en la vista
        Map<LocalDate, String> labelsPorDia = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) labelsPorDia.put(lunes.plusDays(i), ABREV_DIA[i]);

        // Encabezado de rango de semana
        String semanaLabel = lunes.getMonth() == domingo.getMonth()
                ? lunes.getDayOfMonth() + " al " + domingo.format(FMT_MES_ANIO)
                : lunes.format(FMT_DIA_MES) + " al " + domingo.format(FMT_MES_ANIO);

        LocalDate hoy = LocalDate.now();
        model.addAttribute("turnosPorDia",   turnosPorDia);
        model.addAttribute("feriadosPorDia", feriadosPorDia);
        model.addAttribute("labelsPorDia",   labelsPorDia);
        model.addAttribute("semanaLabel",    semanaLabel);
        model.addAttribute("lunes",          lunes);
        model.addAttribute("domingo",        domingo);
        model.addAttribute("hoy",            hoy);
        model.addAttribute("esEstaSemana",   lunes.equals(hoy.with(DayOfWeek.MONDAY)));
        model.addAttribute("semanaAnterior", lunes.minusWeeks(1));
        model.addAttribute("semanaSiguiente",lunes.plusWeeks(1));
        model.addAttribute("estadosTurno",   EstadoTurno.values());
        return "turno/semana";
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevo(Model model,
                        @RequestParam(required = false) Long pacienteId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Turno turno = new Turno();
        if (pacienteId != null) turno.setPaciente(pacienteService.buscarPorId(pacienteId));
        if (fecha != null) {
            turno.setFecha(fecha);
            turnoService.getFeriado(fecha).ifPresent(f -> model.addAttribute("feriadoAviso", f));
        }
        model.addAttribute("turno", turno);
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("pacientes", pacienteService.listarActivos());
        model.addAttribute("estados", EstadoTurno.values());
        return "turno/form";
    }

    // JSON: horarios de atención activos de un médico (usado por turnos-libres.js)
    @GetMapping("/horarios-medico")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<Map<String, Object>> horariosMedico(@RequestParam Long medicoId) {
        var medico = medicoService.buscarPorId(medicoId);
        return horarioRepository.findByMedicoAndActivoTrueOrderByDiaSemanaAscHoraDesdeAsc(medico)
                .stream()
                .map(h -> Map.<String, Object>of(
                        "diaSemana",           h.getDiaSemana().name(),
                        "horaDesde",           h.getHoraDesde().toString(),
                        "horaHasta",           h.getHoraHasta().toString(),
                        "duracionBaseMinutos", h.getDuracionBaseMinutos()
                ))
                .toList();
    }

    // JSON: horas libres para un médico y fecha (usado por turnos-libres.js)
    @GetMapping("/libres")
    @org.springframework.web.bind.annotation.ResponseBody
    public TurnosLibresResponse libres(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "30") Integer duracionMinutos) {

        var medico = medicoService.buscarPorId(medicoId);
        return turnoService.calcularTurnosLibres(medico, fecha, duracionMinutos);
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
