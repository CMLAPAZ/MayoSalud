package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.DiaAtencion;
import ar.com.mayosalud.entity.HorarioAtencionMedico;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.repository.HorarioAtencionMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioAtencionMedicoService {

    private final HorarioAtencionMedicoRepository horarioRepository;

    @Transactional(readOnly = true)
    public List<HorarioAtencionMedico> listarPorMedico(Medico medico) {
        return horarioRepository.findByMedicoOrderByDiaSemanaAscHoraDesdeAsc(medico);
    }

    @Transactional(readOnly = true)
    public Optional<HorarioAtencionMedico> buscarActivoPorMedicoYDia(Medico medico, DayOfWeek dia) {
        return horarioRepository.findByMedicoAndDiaSemanaAndActivoTrue(medico, dia);
    }

    @Transactional(readOnly = true)
    public HorarioAtencionMedico buscarPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con id: " + id));
    }

    public HorarioAtencionMedico guardar(HorarioAtencionMedico horario) {
        if (horario.getHoraDesde() == null || horario.getHoraHasta() == null) {
            throw new RuntimeException("La hora de inicio y fin son obligatorias.");
        }
        if (!horario.getHoraHasta().isAfter(horario.getHoraDesde())) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio.");
        }

        // Validar solapamiento: no puede haber otro horario activo para mismo médico+día
        if (horario.isActivo() && horario.getMedico() != null && horario.getDiaSemana() != null) {
            horarioRepository.findByMedicoAndDiaSemanaAndActivoTrue(
                    horario.getMedico(), horario.getDiaSemana()
            ).ifPresent(existente -> {
                if (!existente.getId().equals(horario.getId())) {
                    String dia = DiaAtencion.NOMBRES.getOrDefault(horario.getDiaSemana(),
                            horario.getDiaSemana().name());
                    throw new RuntimeException(
                            "Ya existe un horario activo para " + dia +
                            ". Desactivalo primero antes de crear uno nuevo.");
                }
            });
        }

        return horarioRepository.save(horario);
    }

    public void desactivar(Long id) {
        HorarioAtencionMedico horario = buscarPorId(id);
        horario.setActivo(false);
        horarioRepository.save(horario);
    }

    public void reactivar(Long id) {
        HorarioAtencionMedico horario = buscarPorId(id);
        // Validar que no haya otro activo para ese médico+día
        horarioRepository.findByMedicoAndDiaSemanaAndActivoTrue(
                horario.getMedico(), horario.getDiaSemana()
        ).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                String dia = DiaAtencion.NOMBRES.getOrDefault(horario.getDiaSemana(),
                        horario.getDiaSemana().name());
                throw new RuntimeException(
                        "Ya existe un horario activo para " + dia +
                        ". Desactivalo primero.");
            }
        });
        horario.setActivo(true);
        horarioRepository.save(horario);
    }
}
