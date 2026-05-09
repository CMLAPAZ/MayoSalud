package ar.com.mayosalud.controller;

import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Sirve el dashboard principal con estadísticas del día y la agenda de hoy. */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TurnoService turnoService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @GetMapping("/")
    public String home(Model model) {
        LocalDate hoy = LocalDate.now();
        String hoyFormateado = hoy.format(
            DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "AR"))
        );
        var turnosHoy = turnoService.listarPorFecha(hoy);
        model.addAttribute("turnosHoy", turnosHoy);
        model.addAttribute("hoyFormateado", hoyFormateado);
        model.addAttribute("totalPacientes", pacienteService.listarActivos().size());
        model.addAttribute("totalMedicos", medicoService.listarActivos().size());
        model.addAttribute("totalTurnosHoy", turnosHoy.size());
        var todosTurnos = turnoService.listarEntreFechas(hoy.minusYears(1), hoy);
        model.addAttribute("ultimoTurno", todosTurnos.isEmpty() ? null : todosTurnos.get(todosTurnos.size() - 1));
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
