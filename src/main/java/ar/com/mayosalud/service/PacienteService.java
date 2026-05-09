package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Lógica de negocio para pacientes: CRUD con validación de DNI único y baja lógica. */
@Service
@RequiredArgsConstructor
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<Paciente> listarActivos() {
        return pacienteRepository.findByActivoTrueOrderByApellidoAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscar(String termino) {
        return pacienteRepository.findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(termino, termino);
    }

    public Paciente guardar(Paciente paciente) {
        pacienteRepository.findByDni(paciente.getDni()).ifPresent(p -> {
            if (!p.getId().equals(paciente.getId()))
                throw new RuntimeException("Ya existe un paciente con el DNI: " + paciente.getDni());
        });
        return pacienteRepository.save(paciente);
    }

    public void darDeBaja(Long id) {
        Paciente paciente = buscarPorId(id);
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
    }
}
