package ar.com.mayosalud.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Locale;

import ar.com.mayosalud.entity.LoginAttempt;
import ar.com.mayosalud.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/** Controla intentos fallidos de login en BD. Bloquea al usuario por BLOQUEO_MINUTOS tras MAX_INTENTOS fallos. */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    public static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MINUTOS = 15;

    private final LoginAttemptRepository repository;

    @Transactional
    public void loginFallido(String username) {
        String usernameKey = normalizarUsername(username);
        LoginAttempt attempt = repository.findByUsername(usernameKey)
                .orElseGet(() -> LoginAttempt.builder()
                        .username(usernameKey)
                        .intentosFallidos(0)
                        .actualizado(LocalDateTime.now())
                        .build());

        int total = attempt.getIntentosFallidos() + 1;
        attempt.setIntentosFallidos(total);
        attempt.setActualizado(LocalDateTime.now());
        if (total >= MAX_INTENTOS) {
            attempt.setBloqueadoDesde(LocalDateTime.now());
        }
        repository.save(attempt);
    }

    @Transactional
    public void loginExitoso(String username) {
        repository.deleteByUsername(normalizarUsername(username));
    }

    @Transactional
    public boolean estaBloqueado(String username) {
        LoginAttempt attempt = repository.findByUsername(normalizarUsername(username)).orElse(null);
        if (attempt == null || attempt.getBloqueadoDesde() == null) return false;

        LocalDateTime desde = attempt.getBloqueadoDesde();
        if (desde.plusMinutes(BLOQUEO_MINUTOS).isBefore(LocalDateTime.now())) {
            repository.delete(attempt);
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public int getIntentos(String username) {
        return repository.findByUsername(normalizarUsername(username))
                .map(LoginAttempt::getIntentosFallidos)
                .orElse(0);
    }

    private String normalizarUsername(String username) {
        if (username == null || username.isBlank()) {
            return "(sin_usuario)";
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
