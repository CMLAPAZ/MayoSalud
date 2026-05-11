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
        LocalDate fecha = turno.getFecha();
        if (fecha != null) {
            if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                throw new RuntimeException("No se pueden crear turnos los domingos.");
            }
            if (feriadoRepository.existsByFechaAndActivoTrue(fecha)) {
                throw new RuntimeException("No se pueden crear turnos en feriados.");
            }
        }

        // Validación de duración permitida
        if (turno.getDuracionMinutos() == null
                || !(turno.getDuracionMinutos() == 15 || turno.getDuracionMinutos() == 30
                     || turno.getDuracionMinutos() == 45 || turno.getDuracionMinutos() == 60)) {
            throw new RuntimeException("Duración inválida. Permitidas: 15/30/45/60 minutos.");
        }

        // Evitar solapamientos por intervalo: mismo médico y misma fecha
        if (turno.getMedico() != null && fecha != null && turno.getHora() != null) {
            List<Turno> turnosDelMedicoEseDia = turnoRepository.findByMedicoAndFechaOrderByHoraAsc(turno.getMedico(), fecha);

            java.time.LocalDateTime nuevoInicio = turno.getHora().atDate(fecha);
            java.time.LocalDateTime nuevoFin = nuevoInicio.plusMinutes(turno.getDuracionMinutos());

            Long editingId = turno.getId();

            for (Turno t : turnosDelMedicoEseDia) {
                if (editingId != null && t.getId() != null && t.getId().equals(editingId)) {
                    continue;
                }

                java.time.LocalDateTime existenteInicio = t.getHora().atDate(fecha);
                int dur = (t.getDuracionMinutos() == null ? 30 : t.getDuracionMinutos());
                java.time.LocalDateTime existenteFin = existenteInicio.plusMinutes(dur);

                boolean solapa = nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio);
                if (solapa) {
                    throw new RuntimeException("El médico tiene un turno que se solapa con el horario seleccionado.");
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

    /**
     * Calcula turnos libres para un médico y fecha, con grilla en pasos de 30 minutos.
     *
     * Nota: se asume que el rango permitido es el que define el frontend (slots fijos).
     * Si la fecha es domingo o feriado, devuelve lista vacía.
     */
    @Transactional(readOnly = true)
    public List<String> calcularTurnosLibres(Medico medico, LocalDate fecha, Integer duracionMinutos) {
        if (fecha == null || medico == null) return List.of();

        if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return List.of();
        }
        if (feriadoRepository.existsByFechaAndActivoTrue(fecha)) {
            return List.of();
        }

        if (duracionMinutos == null) return List.of();
        // Validación simple para evitar requests manipulados
        if (!(duracionMinutos == 15 || duracionMinutos == 30 || duracionMinutos == 45 || duracionMinutos == 60)) {
            return List.of();
        }

        // Slots solicitados por el usuario (inicio del turno cada 30 min)
        List<java.time.LocalTime> slots = List.of(
                java.time.LocalTime.of(8, 0),
                java.time.LocalTime.of(8, 30),
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(9, 30),
                java.time.LocalTime.of(10, 0),
                java.time.LocalTime.of(10, 30),
                java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(11, 30),
                java.time.LocalTime.of(12, 0)
        );

        List<Turno> turnosExistentes = turnoRepository.findByMedicoAndFechaOrderByHoraAsc(medico, fecha);

        return slots.stream()
                .filter(slotInicio -> {
                    java.time.LocalDateTime nuevoInicio = slotInicio.atDate(fecha);
                    java.time.LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracionMinutos);

                    // Libre si NO hay solapamiento con ninguno existente
                    return turnosExistentes.stream().noneMatch(t -> {
                        java.time.LocalDateTime existenteInicio = t.getHora().atDate(fecha);
                        int dur = (t.getDuracionMinutos() == null ? 30 : t.getDuracionMinutos());
                        java.time.LocalDateTime existenteFin = existenteInicio.plusMinutes(dur);

                        // Solapa si inicio < otroFin && fin > otroInicio
                        return nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio);
                    });
                })
                .map(t -> String.format("%02d:%02d", t.getHour(), t.getMinute()))
                .toList();
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
