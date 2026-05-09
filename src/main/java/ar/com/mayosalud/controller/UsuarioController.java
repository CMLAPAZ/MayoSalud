package ar.com.mayosalud.controller;

import ar.com.mayosalud.entity.RolUsuario;
import ar.com.mayosalud.entity.Usuario;
import ar.com.mayosalud.service.AuditoriaService;
import ar.com.mayosalud.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** ABM de usuarios del sistema — acceso restringido a ADMIN. Los usuarios nunca se eliminan físicamente. */
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("esNuevo", true);
        return "usuario/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute Usuario usuario,
                        BindingResult result,
                        @RequestParam String passwordNuevo,
                        @RequestParam String passwordConfirm,
                        Model model,
                        RedirectAttributes redirectAttrs) {
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("esNuevo", true);

        if (result.hasErrors()) return "usuario/form";

        if (passwordNuevo == null || passwordNuevo.length() < 6) {
            model.addAttribute("errorPassword", "La contraseña debe tener al menos 6 caracteres.");
            return "usuario/form";
        }
        if (!passwordNuevo.equals(passwordConfirm)) {
            model.addAttribute("errorPassword", "Las contraseñas no coinciden.");
            return "usuario/form";
        }

        try {
            Usuario nuevo = usuarioService.crear(usuario, passwordNuevo);
            auditoriaService.registrar("ALTA_USUARIO", "Usuario", nuevo.getId().toString(),
                    "Creó usuario: " + nuevo.getUsername() + " (" + nuevo.getRol().getDescripcion() + ")");
            redirectAttrs.addFlashAttribute("exito", "Usuario creado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("esNuevo", false);
        return "usuario/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Usuario usuario,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttrs) {
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("esNuevo", false);
        if (result.hasErrors()) return "usuario/form";

        usuario.setId(id);
        try {
            usuarioService.actualizar(usuario);
            auditoriaService.registrar("EDICION_USUARIO", "Usuario", id.toString(),
                    "Editó usuario: " + usuario.getUsername());
            redirectAttrs.addFlashAttribute("exito", "Usuario actualizado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/cambiar-password/{id}")
    public String formCambiarPassword(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuario/cambiar-password";
    }

    @PostMapping("/cambiar-password/{id}")
    public String cambiarPassword(@PathVariable Long id,
                                  @RequestParam String passwordNuevo,
                                  @RequestParam String passwordConfirm,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {
        if (passwordNuevo == null || passwordNuevo.length() < 6) {
            model.addAttribute("usuario", usuarioService.buscarPorId(id));
            model.addAttribute("errorPassword", "La contraseña debe tener al menos 6 caracteres.");
            return "usuario/cambiar-password";
        }
        if (!passwordNuevo.equals(passwordConfirm)) {
            model.addAttribute("usuario", usuarioService.buscarPorId(id));
            model.addAttribute("errorPassword", "Las contraseñas no coinciden.");
            return "usuario/cambiar-password";
        }
        usuarioService.cambiarPassword(id, passwordNuevo);
        Usuario u = usuarioService.buscarPorId(id);
        auditoriaService.registrar("CAMBIO_PASSWORD", "Usuario", id.toString(),
                "Cambió contraseña de: " + u.getUsername());
        redirectAttrs.addFlashAttribute("exito", "Contraseña actualizada correctamente.");
        return "redirect:/usuarios";
    }

    /** Activa o desactiva el usuario. Nunca se elimina para preservar el historial. */
    @PostMapping("/toggle-activo/{id}")
    public String toggleActivo(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails admin,
                               RedirectAttributes redirectAttrs) {
        Usuario u = usuarioService.buscarPorId(id);
        if (admin.getUsername().equals(u.getUsername())) {
            redirectAttrs.addFlashAttribute("error", "No podés desactivar tu propia cuenta.");
            return "redirect:/usuarios";
        }
        usuarioService.toggleActivo(id);
        u = usuarioService.buscarPorId(id);
        auditoriaService.registrar("TOGGLE_USUARIO", "Usuario", id.toString(),
                "Cambió estado a " + (u.isActivo() ? "ACTIVO" : "INACTIVO") + ": " + u.getUsername());
        redirectAttrs.addFlashAttribute("exito",
                "Usuario " + (u.isActivo() ? "activado" : "desactivado") + " correctamente.");
        return "redirect:/usuarios";
    }
}
