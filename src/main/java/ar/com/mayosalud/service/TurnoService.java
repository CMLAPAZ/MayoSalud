package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.repository.FeriadoRepository;
import ar.com.mayosalud.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Lógica de negocio para turnos: agenda diaria, validación de conflicto de horario y cambio de estado. */
@Service
@RequiredArgsConstructor
@Transactional
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final FeriadoRepository feriadoRepository;

    @Transactional(readOnly = true)
    public List<Turno> listarPorFecha(LocalDate fecha) {
        return turnoRepository.findByFechaOrderByHoraAsc(fecha);
    }

    @Transactional(readOnly = true)
    public List<Turno> listarPorMedicoYFecha(Medico medico, LocalDate fecha) {
        return turnoRepository.findByMedicoAndFechaOrderByHoraAsc(medico, fecha);
    }

    @Transactional(readOnly = true)
    public List<Turno> listarPorPaciente(Paciente paciente) {
        return turnoRepository.findByPacienteOrderByFechaDescHoraDesc(paciente);
    }

    @Transactional(readOnly = true)
    public List<Turno> listarEntreFechas(LocalDate desde, LocalDate hasta) {
        return turnoRepository.findByFechaBetweenOrderByFechaAscHoraAsc(desde, hasta);
    }

    @Transactional(readOnly = true)
    public Turno buscarPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con id: " + id));
    }

    public Turno guardar(Turno turno) {
        boolean existe = turnoRepository.existsByMedicoAndFechaAndHora(
                turno.getMedico(), turno.getFecha(), turno.getHora());
        if (existe && (turno.getId() == null)) {
            throw new RuntimeException("El médico ya tiene un turno asignado en ese horario.");
        }
        return turnoRepository.save(turno);
    }

    public void cambiarEstado(Long id, EstadoTurno nuevoEstado) {
        Turno turno = buscarPorId(id);
        turno.setEstado(nuevoEstado);
        turnoRepository.save(turno);
    }

    public void eliminar(Long id) {
        turnoRepository.deleteById(id);
    }

    /** Devuelve el feriado si la fecha lo es, o vacío si es día hábil. */
    @Transactional(readOnly = true)
    public Optional<Feriado> getFeriado(LocalDate fecha) {
        return feriadoRepository.findByFechaAndActivoTrue(fecha);
    }

    /** Devuelve los feriados activos de un rango de fechas indexados por fecha. */
    @Transactional(readOnly = true)
    public Map<LocalDate, Feriado> getFeriadosSemana(LocalDate desde, LocalDate hasta) {
        return feriadoRepository.findByFechaBetweenOrderByFechaAsc(desde, hasta)
                .stream()
                .filter(Feriado::isActivo)
                .collect(Collectors.toMap(Feriado::getFecha, f -> f));
    }
}
