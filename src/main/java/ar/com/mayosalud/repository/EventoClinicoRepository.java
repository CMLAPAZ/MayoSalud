package ar.com.mayosalud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.mayosalud.entity.EventoClinico;
import ar.com.mayosalud.entity.Paciente;

@Repository
public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Long> {

    List<EventoClinico> findTop20ByPacienteOrderByFechaHoraDesc(Paciente paciente);
}

