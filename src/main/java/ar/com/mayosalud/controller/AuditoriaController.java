package ar.com.mayosalud.controller;

import ar.com.mayosalud.service.AuditoriaService;
import ar.com.mayosalud.service.RecordatorioScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/** Expone el registro de auditoría con filtros por fecha y usuario. Acceso restringido a ADMIN. */
@Controller
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;
    private final RecordatorioScheduler recordatorioScheduler;

    @GetMapping
    public String lista(Model model,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                        @RequestParam(required = false) String usuario) {
        model.addAttribute("logs", auditoriaService.listar(desde, hasta, usuario));
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("usuario", usuario);
        return "auditoria/lista";
    }

    @PostMapping("/recordatorios/enviar")
    public String enviarRecordatoriosManual(RedirectAttributes ra) {
        try {
            recordatorioScheduler.enviarRecordatorios();
            ra.addFlashAttribute("exito", "Recordatorios procesados. Revisá los últimos registros de la auditoría.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar recordatorios: " + e.getMessage());
        }
        return "redirect:/auditoria";
    }
}
