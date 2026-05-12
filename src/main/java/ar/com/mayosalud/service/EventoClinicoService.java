package ar.com.mayosalud.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.mayosalud.entity.EstadoEstudio;
import ar.com.mayosalud.entity.EstudioDetalle;
import ar.com.mayosalud.entity.EventoClinico;
import ar.com.mayosalud.entity.EvolucionMedicaDetalle;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.SignosVitalesDetalle;
import ar.com.mayosalud.entity.TipoEventoClinico;
import ar.com.mayosalud.entity.Usuario;
import ar.com.mayosalud.repository.EstudioDetalleRepository;
import ar.com.mayosalud.repository.EventoClinicoRepository;
import ar.com.mayosalud.repository.EvolucionMedicaDetalleRepository;
import ar.com.mayosalud.repository.SignosVitalesDetalleRepository;
import ar.com.mayosalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoClinicoService {

    private final EventoClinicoRepository eventoRepository;
    private final SignosVitalesDetalleRepository signosRepository;
    private final EvolucionMedicaDetalleRepository evolucionRepository;
    private final EstudioDetalleRepository estudioRepository;

    private final PacienteService pacienteService;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<EventoClinico> listarUltimos20PorPaciente(Paciente paciente) {
        return eventoRepository.findTop20ByPacienteOrderByFechaHoraDesc(paciente);
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : null;
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public EventoClinico guardarSignosVitales(Long pacienteId,
                                               Double temperatura,
                                               Integer presionSistolica,
                                               Integer presionDiastolica,
                                               Integer pulsoFrecuencia,
                                               Double saturacionOxigeno,
                                               Double peso,
                                               Double talla,
                                               String observacionesEspecificas) {

        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        Usuario usuario = usuarioActual();

        EventoClinico evento = new EventoClinico();
        evento.setPaciente(paciente);
        evento.setTurno(null);
        evento.setUsuarioRegistrante(usuario);
        evento.setFechaHora(LocalDateTime.now());
        evento.setTipo(TipoEventoClinico.SIGNOS_VITALES);
        evento.setTitulo("Signos vitales");
        evento.setDescripcionGeneral(null);
        evento.setObservaciones(null);
        eventoRepository.save(evento);

        SignosVitalesDetalle detalle = new SignosVitalesDetalle();
        detalle.setEvento(evento);
        detalle.setTemperatura(temperatura);
        detalle.setPresionSistolica(presionSistolica);
        detalle.setPresionDiastolica(presionDiastolica);
        detalle.setPulsoFrecuencia(pulsoFrecuencia);
        detalle.setSaturacionOxigeno(saturacionOxigeno);
        detalle.setPeso(peso);
        detalle.setTalla(talla);
        detalle.setObservacionesEspecificas(observacionesEspecificas);
        signosRepository.save(detalle);

        return evento;
    }

    public EventoClinico guardarEvolucionMedica(Long pacienteId,
                                                   String motivoConsulta,
                                                   String evolucionTexto,
                                                   String examenFisico,
                                                   String impresionDiagnostica,
                                                   String conducta,
                                                   String indicaciones) {

        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        Usuario usuario = usuarioActual();

        EventoClinico evento = new EventoClinico();
        evento.setPaciente(paciente);
        evento.setTurno(null);
        evento.setUsuarioRegistrante(usuario);
        evento.setFechaHora(LocalDateTime.now());
        evento.setTipo(TipoEventoClinico.EVOLUCION_MEDICA);
        evento.setTitulo("Evolución médica");
        evento.setDescripcionGeneral(null);
        evento.setObservaciones(null);
        eventoRepository.save(evento);

        EvolucionMedicaDetalle detalle = new EvolucionMedicaDetalle();
        detalle.setEvento(evento);
        detalle.setMotivoConsulta(motivoConsulta);
        detalle.setEvolucionTexto(evolucionTexto);
        detalle.setExamenFisico(examenFisico);
        detalle.setImpresionDiagnostica(impresionDiagnostica);
        detalle.setConducta(conducta);
        detalle.setIndicaciones(indicaciones);
        evolucionRepository.save(detalle);

        return evento;
    }

    public EventoClinico guardarEstudio(Long pacienteId,
                                          String tipoEstudio,
                                          String nombreEstudio,
                                          String indicacion,
                                          EstadoEstudio estado,
                                          LocalDate fechaSolicitud,
                                          LocalDate fechaRealizacion,
                                          String resultadoTexto,
                                          String resultadoUrl) {

        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        Usuario usuario = usuarioActual();

        EventoClinico evento = new EventoClinico();
        evento.setPaciente(paciente);
        evento.setTurno(null);
        evento.setUsuarioRegistrante(usuario);
        evento.setFechaHora(LocalDateTime.now());
        evento.setTipo(TipoEventoClinico.ESTUDIO);
        evento.setTitulo("Estudio");
        evento.setDescripcionGeneral(nombreEstudio);
        evento.setObservaciones(null);
        eventoRepository.save(evento);

        EstudioDetalle detalle = new EstudioDetalle();
        detalle.setEvento(evento);
        detalle.setTipoEstudio(tipoEstudio);
        detalle.setNombreEstudio(nombreEstudio);
        detalle.setIndicacion(indicacion);
        detalle.setEstado(estado);
        detalle.setFechaSolicitud(fechaSolicitud);
        detalle.setFechaRealizacion(fechaRealizacion);
        detalle.setResultadoTexto(resultadoTexto);
        detalle.setResultadoUrl(resultadoUrl);
        estudioRepository.save(detalle);

        return evento;
    }
}

