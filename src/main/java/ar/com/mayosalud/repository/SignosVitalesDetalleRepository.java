package ar.com.mayosalud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.mayosalud.entity.SignosVitalesDetalle;

@Repository
public interface SignosVitalesDetalleRepository extends JpaRepository<SignosVitalesDetalle, Long> {
}

