package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/** Acceso a datos de turnos. existsByMedicoAndFechaAndHora se usa para prevenir solapamiento de horarios. */
@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByFechaOrderByHoraAsc(LocalDate fecha);
    List<Turno> findByMedicoAndFechaOrderByHoraAsc(Medico medico, LocalDate fecha);
    List<Turno> findByMedicoAndFechaBetweenOrderByFechaAscHoraAsc(Medico medico, LocalDate desde, LocalDate hasta);
    List<Turno> findByMedicoOrderByFechaDescHoraDesc(Medico medico);
    List<Turno> findByPacienteOrderByFechaDescHoraDesc(Paciente paciente);
    boolean existsByMedicoAndPaciente(Medico medico, Paciente paciente);
    List<Turno> findByFechaBetweenOrderByFechaAscHoraAsc(LocalDate desde, LocalDate hasta);
    List<Turno> findByEstadoAndFechaOrderByHoraAsc(EstadoTurno estado, LocalDate fecha);
    boolean existsByMedicoAndFechaAndHora(Medico medico, LocalDate fecha, java.time.LocalTime hora);
}
