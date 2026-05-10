package ar.com.mayosalud.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.repository.FeriadoRepository;
import ar.com.mayosalud.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;

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
        // Evitar solapamientos: mismo médico, misma fecha, misma hora
        boolean existeMismaHora = turnoRepository.existsByMedicoAndFechaAndHora(
                turno.getMedico(), turno.getFecha(), turno.getHora());
        if (existeMismaHora && (turno.getId() == null)) {
            throw new RuntimeException("El médico ya tiene un turno asignado en ese horario.");
        }

        // Regla de distancia mínima (30 minutos) para el mismo profesional
        // (se aplica siempre: no depende del estado)
        LocalDate fecha = turno.getFecha();
        if (fecha != null) {
            if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                throw new RuntimeException("No se pueden crear turnos los domingos.");
            }
            if (feriadoRepository.existsByFechaAndActivoTrue(fecha)) {
                throw new RuntimeException("No se pueden crear turnos en feriados.");
            }

            List<Turno> turnosDelMedicoEseDia = turnoRepository.findByMedicoAndFechaOrderByHoraAsc(
                    turno.getMedico(), fecha);

            // Si se edita un turno existente, ignorar el propio id
            Long editingId = turno.getId();

            for (Turno t : turnosDelMedicoEseDia) {
                if (editingId != null && t.getId() != null && t.getId().equals(editingId)) {
                    continue;
                }

                long minutosEntre = Math.abs(java.time.Duration.between(t.getHora().atDate(fecha),
                                                                        turno.getHora().atDate(fecha)).toMinutes());

                if (minutosEntre < 30) {
                    throw new RuntimeException(
                            "El médico ya tiene un turno a menos de 30 minutos. Debe existir una distancia mínima de 30 minutos entre turnos.");
                }
            }
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
