package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acceso a datos para bloqueos e intentos fallidos de login. */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    Optional<LoginAttempt> findByUsername(String username);

    void deleteByUsername(String username);
}
