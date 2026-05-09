package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/** Acceso a registros de auditoría con soporte de filtros por fecha y usuario. */
@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    List<AuditoriaLog> findAllByOrderByFechaHoraDesc();

    List<AuditoriaLog> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<AuditoriaLog> findByUsuarioContainingIgnoreCaseOrderByFechaHoraDesc(
            String usuario);

    List<AuditoriaLog> findByFechaHoraBetweenAndUsuarioContainingIgnoreCaseOrderByFechaHoraDesc(
            LocalDateTime desde, LocalDateTime hasta, String usuario);
}
