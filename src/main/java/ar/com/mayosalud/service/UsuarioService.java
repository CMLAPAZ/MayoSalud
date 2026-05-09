package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.RolUsuario;
import ar.com.mayosalud.entity.Usuario;
import ar.com.mayosalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** Gestión de usuarios: altas, modificaciones, cambio de contraseña y activación/desactivación. */
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

    /** Crea un usuario nuevo codificando su contraseña en texto plano. */
    public Usuario crear(Usuario usuario, String passwordPlano) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
        }
        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        return usuarioRepository.save(usuario);
    }

    /** Actualiza datos de un usuario existente sin modificar su contraseña. */
    public Usuario actualizar(Usuario usuario) {
        Usuario existente = buscarPorId(usuario.getId());
        if (usuarioRepository.existsByUsernameAndIdNot(usuario.getUsername(), usuario.getId())) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
        }
        existente.setUsername(usuario.getUsername());
        existente.setNombreCompleto(usuario.getNombreCompleto());
        existente.setEmail(usuario.getEmail());
        existente.setRol(usuario.getRol());
        return usuarioRepository.save(existente);
    }

    public void cambiarPassword(Long id, String passwordNuevo) {
        Usuario usuario = buscarPorId(id);
        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
    }

    /** Activa o desactiva el usuario. No se permite eliminar usuarios con historial. */
    public void toggleActivo(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    /** Registra la fecha y hora del último acceso exitoso. */
    public void registrarAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            u.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(u);
        });
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
