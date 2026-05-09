package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de pacientes con queries derivadas de Spring Data JPA. */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByActivoTrueOrderByApellidoAscNombreAsc();
    Optional<Paciente> findByDni(String dni);
    List<Paciente> findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(String apellido, String nombre);
}
