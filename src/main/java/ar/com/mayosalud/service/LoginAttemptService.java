package ar.com.mayosalud.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/** Controla intentos fallidos de login en memoria. Bloquea al usuario por BLOQUEO_MINUTOS tras MAX_INTENTOS fallos. */
@Service
public class LoginAttemptService {

    public static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MINUTOS = 15;

    private final ConcurrentHashMap<String, Integer> intentos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> bloqueados = new ConcurrentHashMap<>();

    public void loginFallido(String username) {
        int total = intentos.merge(username, 1, Integer::sum);
        if (total >= MAX_INTENTOS) {
            bloqueados.put(username, LocalDateTime.now());
        }
    }

    public void loginExitoso(String username) {
        intentos.remove(username);
        bloqueados.remove(username);
    }

    public boolean estaBloqueado(String username) {
        LocalDateTime desde = bloqueados.get(username);
        if (desde == null) return false;
        if (desde.plusMinutes(BLOQUEO_MINUTOS).isBefore(LocalDateTime.now())) {
            bloqueados.remove(username);
            intentos.remove(username);
            return false;
        }
        return true;
    }

    public int getIntentos(String username) {
        return intentos.getOrDefault(username, 0);
    }
}
