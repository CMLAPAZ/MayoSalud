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
import java.util.Optional;

/** Gestion de usuarios: altas, modificaciones, cambio de contrasena y activacion/desactivacion. */
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

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /** Crea un usuario nuevo codificando su contrasena en texto plano. */
    public Usuario crear(Usuario usuario, String passwordPlano) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya esta en uso.");
        }
        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        return usuarioRepository.save(usuario);
    }

    /** Actualiza datos de un usuario existente sin modificar su contrasena. */
    public Usuario actualizar(Usuario usuario) {
        Usuario existente = buscarPorId(usuario.getId());
        if (usuarioRepository.existsByUsernameAndIdNot(usuario.getUsername(), usuario.getId())) {
            throw new RuntimeException("El nombre de usuario '" + usuario.getUsername() + "' ya esta en uso.");
        }
        existente.setUsername(usuario.getUsername());
        existente.setNombreCompleto(usuario.getNombreCompleto());
        existente.setEmail(usuario.getEmail());
        existente.setRol(usuario.getRol());
        existente.setMedico(usuario.getMedico());
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

    /** Registra la fecha y hora del ultimo acceso exitoso. */
    public void registrarAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            u.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(u);
        });
    }

    /**
     * Crea un admin inicial solo si se configura por variables de entorno.
     * Nunca modifica usuarios existentes ni resetea contrasenas en cada arranque.
     */
    public void inicializarAdmin(String username, String password, String nombreCompleto) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }

        Usuario admin = Usuario.builder()
                .username(username.trim())
                .password(passwordEncoder.encode(password))
                .nombreCompleto((nombreCompleto == null || nombreCompleto.isBlank())
                        ? "Administrador del Sistema"
                        : nombreCompleto.trim())
                .rol(RolUsuario.ADMIN)
                .activo(true)
                .build();
        usuarioRepository.save(admin);
    }
}
