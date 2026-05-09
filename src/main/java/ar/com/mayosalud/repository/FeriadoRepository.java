package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de feriados. */
@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    List<Feriado> findAllByOrderByFechaAsc();
    List<Feriado> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
    Optional<Feriado> findByFechaAndActivoTrue(LocalDate fecha);
    boolean existsByFechaAndActivoTrue(LocalDate fecha);
    boolean existsByFecha(LocalDate fecha);
}
