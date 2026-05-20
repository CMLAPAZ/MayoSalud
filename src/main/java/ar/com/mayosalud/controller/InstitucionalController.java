package ar.com.mayosalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ar.com.mayosalud.service.MedicoService;
import ar.com.mayosalud.service.PacienteService;
import ar.com.mayosalud.service.TurnoService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para páginas institucionales públicas.
 * No requiere autenticación.
 */
@Controller
@RequiredArgsConstructor
public class InstitucionalController {

    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final TurnoService turnoService;

    /**
     * Landing page institucional - pública sin autenticación
     */
    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("medicosActivos", medicoService.listarActivos());
        model.addAttribute("totalPacientes", pacienteService.listarActivos().size());
        model.addAttribute("totalMedicos", medicoService.listarActivos().size());
        model.addAttribute("totalTurnos", turnoService.listarTodos().size());
        return "institucional/landing";
    }

    /**
     * Página "Sobre nosotros"
     */
    @GetMapping("/nosotros")
    public String nosotros() {
        return "institucional/nosotros";
    }

    /**
     * Página de especialidades
     */
    @GetMapping("/especialidades")
    public String especialidades(Model model) {
        model.addAttribute("medicosActivos", medicoService.listarActivos());
        return "institucional/especialidades";
    }

    /**
     * Página de médicos socios con galería
     */
    @GetMapping("/profesionales")
    public String medicos(Model model) {
        model.addAttribute("medicosActivos", medicoService.listarActivos());
        return "institucional/medicos";
    }

    /**
     * Portal público del paciente
     */
    @GetMapping("/portal")
    public String portal() {
        return "institucional/portal";
    }

    /**
     * Equipo: socios fundadores y personal de la clínica
     */
    @GetMapping("/equipo")
    public String equipo(Model model) {
        model.addAttribute("medicosActivos", medicoService.listarActivos());
        return "institucional/equipo";
    }

    /**
     * Página de contacto
     */
    @GetMapping("/contacto")
    public String contacto() {
        return "institucional/contacto";
    }
}
