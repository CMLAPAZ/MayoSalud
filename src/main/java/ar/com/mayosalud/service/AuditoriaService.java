package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.AuditoriaLog;
import ar.com.mayosalud.repository.AuditoriaLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Registra y consulta entradas de auditoría. Resuelve usuario e IP desde el contexto de seguridad y request actuales. */
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository repository;

    public void registrar(String accion, String entidad, String entidadId, String detalle) {
        String usuario = obtenerUsuario();
        String ip = obtenerIp();

        AuditoriaLog log = AuditoriaLog.builder()
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .usuario(usuario)
                .ip(ip)
                .fechaHora(LocalDateTime.now())
                .build();

        repository.save(log);
    }

    public void registrar(String accion, String entidad, String entidadId, String detalle, String usuario, String ip) {
        AuditoriaLog log = AuditoriaLog.builder()
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .usuario(usuario)
                .ip(ip)
                .fechaHora(LocalDateTime.now())
                .build();
        repository.save(log);
    }

    public List<AuditoriaLog> listar(LocalDate desde, LocalDate hasta, String usuario) {
        boolean tieneUsuario = usuario != null && !usuario.isBlank();
        boolean tieneFecha = desde != null && hasta != null;

        if (tieneFecha && tieneUsuario) {
            return repository.findByFechaHoraBetweenAndUsuarioContainingIgnoreCaseOrderByFechaHoraDesc(
                    desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), usuario);
        } else if (tieneFecha) {
            return repository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                    desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
        } else if (tieneUsuario) {
            return repository.findByUsuarioContainingIgnoreCaseOrderByFechaHoraDesc(usuario);
        } else {
            return repository.findAllByOrderByFechaHoraDesc();
        }
    }

    private String obtenerUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "sistema";
    }

    private String obtenerIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "N/A";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim()
                    : request.getRemoteAddr();
        } catch (Exception e) {
            return "N/A";
        }
    }
}
