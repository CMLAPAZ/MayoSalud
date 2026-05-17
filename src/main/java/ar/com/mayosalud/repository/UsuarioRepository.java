package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de usuarios del sistema. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAllByOrderByNombreCompletoAsc();

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
