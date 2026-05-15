package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.Especialidad;
import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Lógica de negocio para médicos: CRUD con validación de DNI y matrícula únicos, y baja lógica. */
@Service
@RequiredArgsConstructor
@Transactional
public class MedicoService {

    private final MedicoRepository medicoRepository;

    @Transactional(readOnly = true)
    public List<Medico> listarActivos() {
        return medicoRepository.findByActivoTrueOrderByApellidoAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Medico> buscarPorEspecialidad(Especialidad especialidad) {
        return medicoRepository.findByEspecialidadAndActivoTrue(especialidad);
    }

    @Transactional(readOnly = true)
    public List<Medico> buscar(String termino) {
        return medicoRepository.findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(termino, termino);
    }

    public Medico guardar(Medico medico) {
        medicoRepository.findByDni(medico.getDni()).ifPresent(m -> {
            if (!m.getId().equals(medico.getId()))
                throw new RuntimeException("Ya existe un médico con el DNI: " + medico.getDni());
        });
        medicoRepository.findByMatricula(medico.getMatricula()).ifPresent(m -> {
            if (!m.getId().equals(medico.getId()))
                throw new RuntimeException("Ya existe un médico con la matrícula: " + medico.getMatricula());
        });
        return medicoRepository.save(medico);
    }

    public void darDeBaja(Long id) {
        Medico medico = buscarPorId(id);
        medico.setActivo(false);
        medicoRepository.save(medico);
    }

    public void reactivar(Long id) {
        Medico medico = buscarPorId(id);
        medico.setActivo(true);
        medicoRepository.save(medico);
    }
}
