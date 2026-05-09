package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.RolUsuario;
import ar.com.mayosalud.entity.Usuario;
import ar.com.mayosalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Gestión de usuarios: altas, bajas, modificaciones, cambio de contraseña y toggle de estado. */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNombreCompletoAsc();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public Usuario guardar(Usuario usuario) {
        boolean usernameEnUso = usuarioRepository.existsByUsernameAndIdNot(
                usuario.getUsername(), usuario.getId() == null ? -1L : usuario.getId());
        if (usernameEnUso) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
        }
        return usuarioRepository.save(usuario);
    }

    /** Crea un usuario nuevo codificando su contraseña en texto plano. */
    public Usuario crear(Usuario usuario, String passwordPlano) {
        boolean usernameEnUso = usuarioRepository.existsByUsername(usuario.getUsername());
        if (usernameEnUso) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
        }
        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        return usuarioRepository.save(usuario);
    }

    /** Actualiza datos de un usuario existente sin tocar la contraseña. */
    public Usuario actualizar(Usuario usuario) {
        Usuario existente = buscarPorId(usuario.getId());
        boolean usernameEnUso = usuarioRepository.existsByUsernameAndIdNot(usuario.getUsername(), usuario.getId());
        if (usernameEnUso) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
        }
        existente.setUsername(usuario.getUsername());
        existente.setNombreCompleto(usuario.getNombreCompleto());
        existente.setRol(usuario.getRol());
        return usuarioRepository.save(existente);
    }

    public void cambiarPassword(Long id, String passwordNuevo) {
        Usuario usuario = buscarPorId(id);
        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
    }

    public void toggleActivo(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    /** Crea el usuario admin inicial si no existe ningún usuario en la base. */
    public void inicializarAdmin() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombreCompleto("Administrador del Sistema")
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
        }
    }
}
