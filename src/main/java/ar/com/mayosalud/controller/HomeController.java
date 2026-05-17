package ar.com.mayosalud.controller;

import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import ar.com.mayosalud.service.UsuarioService;
import ar.com.mayosalud.entity.Medico;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        LocalDate hoy = LocalDate.now();
        String hoyFormateado = hoy.format(
            DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "AR"))
        );
        boolean filtrarPorMedico = esRolMedico(authentication);
        Medico medicoActual = medicoActualSiRolMedico(authentication);
        var turnosHoy = filtrarPorMedico
                ? turnoService.listarPorMedicoYFecha(medicoActual, hoy)
                : turnoService.listarPorFecha(hoy);
        model.addAttribute("turnosHoy", turnosHoy);
        model.addAttribute("hoyFormateado", hoyFormateado);
        model.addAttribute("totalPacientes", pacienteService.listarActivos().size());
        model.addAttribute("totalMedicos", medicoService.listarActivos().size());
        model.addAttribute("totalTurnosHoy", turnosHoy.size());
        var todosTurnos = filtrarPorMedico
                ? turnoService.listarPorMedicoEntreFechas(medicoActual, hoy.minusYears(1), hoy)
                : turnoService.listarEntreFechas(hoy.minusYears(1), hoy);
        model.addAttribute("ultimoTurno", todosTurnos.isEmpty() ? null : todosTurnos.get(todosTurnos.size() - 1));
        if (filtrarPorMedico) {
            model.addAttribute("agendaFiltradaMedico", true);
            model.addAttribute("medicoAgenda", medicoActual);
        }
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
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
