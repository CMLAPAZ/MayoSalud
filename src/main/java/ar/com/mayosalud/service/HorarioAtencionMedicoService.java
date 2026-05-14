package ar.com.mayosalud.service;

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
        if (horario.getHoraDesde() != null && horario.getHoraHasta() != null
                && !horario.getHoraHasta().isAfter(horario.getHoraDesde())) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        return horarioRepository.save(horario);
    }

    public void eliminar(Long id) {
        horarioRepository.deleteById(id);
    }
}
