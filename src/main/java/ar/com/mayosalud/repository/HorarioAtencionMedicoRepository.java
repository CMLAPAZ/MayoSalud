package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.HorarioAtencionMedico;
import ar.com.mayosalud.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioAtencionMedicoRepository extends JpaRepository<HorarioAtencionMedico, Long> {
    List<HorarioAtencionMedico> findByMedicoOrderByDiaSemanaAscHoraDesdeAsc(Medico medico);
    Optional<HorarioAtencionMedico> findByMedicoAndDiaSemanaAndActivoTrue(Medico medico, DayOfWeek diaSemana);
}
