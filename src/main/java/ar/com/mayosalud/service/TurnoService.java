package ar.com.mayosalud.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.mayosalud.dto.TurnosLibresResponse;
import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.HorarioAtencionMedico;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.repository.FeriadoRepository;
import ar.com.mayosalud.repository.HorarioAtencionMedicoRepository;
import ar.com.mayosalud.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;

/** Lógica de negocio para turnos: agenda diaria, validación de conflicto de horario y cambio de estado. */
@Service
@RequiredArgsConstructor
@Transactional
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final FeriadoRepository feriadoRepository;
    private final HorarioAtencionMedicoRepository horarioRepository;

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

        int duracionMinutos = (turno.getDuracionMinutos() != null) ? turno.getDuracionMinutos() : 30;
        if (!ar.com.mayosalud.entity.TurnoDuracion.esPermitida(duracionMinutos)) {
            throw new RuntimeException("Duración inválida. Permitidas: 15/30/45/60");
        }

        // Evitar solapamientos por intervalo: mismo médico y misma fecha
        if (turno.getMedico() != null && fecha != null && turno.getHora() != null) {
            List<Turno> turnosDelMedicoEseDia =
                    turnoRepository.findByMedicoAndFechaOrderByHoraAsc(turno.getMedico(), fecha);

            java.time.LocalDateTime nuevoInicio = turno.getHora().atDate(fecha);
            java.time.LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracionMinutos);

            Long editingId = turno.getId();

            for (Turno t : turnosDelMedicoEseDia) {
                if (editingId != null && t.getId() != null && t.getId().equals(editingId)) {
                    continue;
                }

                int durExistente = (t.getDuracionMinutos() != null) ? t.getDuracionMinutos() : 30;
                java.time.LocalDateTime existenteInicio = t.getHora().atDate(fecha);
                java.time.LocalDateTime existenteFin = existenteInicio.plusMinutes(durExistente);

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
     * Calcula turnos libres para un médico y fecha.
     * Los slots se generan desde el horario de atención configurado para ese día.
     * Si no hay horario activo, devuelve listas vacías.
     */
    @Transactional(readOnly = true)
    public TurnosLibresResponse calcularTurnosLibres(Medico medico, LocalDate fecha, Integer duracionMinutos) {

        org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TurnoService.class);

        if (fecha == null || medico == null) {
            return new TurnosLibresResponse(List.of(), List.of());
        }

        if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return new TurnosLibresResponse(List.of(), List.of());
        }

        if (feriadoRepository.existsByFechaAndActivoTrue(fecha)) {
            return new TurnosLibresResponse(List.of(), List.of());
        }

        int duracionRaw = (duracionMinutos != null) ? duracionMinutos : 30;
        final int duracion = ar.com.mayosalud.entity.TurnoDuracion.esPermitida(duracionRaw) ? duracionRaw : 30;

        // Buscar horario activo del médico para el día de semana de la fecha
        Optional<HorarioAtencionMedico> horarioOpt =
                horarioRepository.findByMedicoAndDiaSemanaAndActivoTrue(medico, fecha.getDayOfWeek());

        if (horarioOpt.isEmpty()) {
            log.debug("[HORARIO] Sin horario activo para medicoId={} dia={}", medico.getId(), fecha.getDayOfWeek());
            return new TurnosLibresResponse(List.of(), List.of());
        }

        HorarioAtencionMedico horario = horarioOpt.get();

        // Generar todos los slots posibles: desde horaDesde hasta horaHasta, paso = duracion
        List<java.time.LocalTime> todosSlots = new ArrayList<>();
        java.time.LocalTime cursor = horario.getHoraDesde();
        while (!cursor.plusMinutes(duracion).isAfter(horario.getHoraHasta())) {
            todosSlots.add(cursor);
            cursor = cursor.plusMinutes(duracion);
        }

        List<Turno> turnosExistentes = turnoRepository.findByMedicoAndFechaOrderByHoraAsc(medico, fecha);

        // Filtrar slots libres: sin solapamiento con turnos existentes
        List<java.time.LocalTime> libresSlots = todosSlots.stream()
                .filter(slotInicio -> {
                    java.time.LocalDateTime nuevoInicio = slotInicio.atDate(fecha);
                    java.time.LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracion);
                    return turnosExistentes.stream().noneMatch(t -> {
                        java.time.LocalDateTime existenteInicio = t.getHora().atDate(fecha);
                        int durExistente = (t.getDuracionMinutos() != null) ? t.getDuracionMinutos() : 30;
                        java.time.LocalDateTime existenteFin = existenteInicio.plusMinutes(durExistente);
                        // Solapa si inicio < otroFin && fin > otroInicio
                        return nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio);
                    });
                })
                .toList();

        List<String> todosList = todosSlots.stream()
                .map(t -> String.format("%02d:%02d", t.getHour(), t.getMinute()))
                .toList();

        List<String> libresList = libresSlots.stream()
                .map(t -> String.format("%02d:%02d", t.getHour(), t.getMinute()))
                .toList();

        log.debug("[HORARIO] medicoId={} fecha={} duracion={} todos={} libres={}",
                medico.getId(), fecha, duracion, todosList.size(), libresList.size());

        return new TurnosLibresResponse(todosList, libresList);
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
