package ar.com.mayosalud.repository;

import ar.com.mayosalud.entity.Especialidad;
import ar.com.mayosalud.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de médicos con queries derivadas de Spring Data JPA. */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByActivoTrueOrderByApellidoAscNombreAsc();
    List<Medico> findByEspecialidadAndActivoTrue(Especialidad especialidad);
    Optional<Medico> findByDni(String dni);
    Optional<Medico> findByMatricula(String matricula);
    List<Medico> findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(String apellido, String nombre);
}
