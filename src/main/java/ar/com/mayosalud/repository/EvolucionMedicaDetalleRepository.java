package ar.com.mayosalud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.mayosalud.entity.EvolucionMedicaDetalle;

@Repository
public interface EvolucionMedicaDetalleRepository extends JpaRepository<EvolucionMedicaDetalle, Long> {
}

