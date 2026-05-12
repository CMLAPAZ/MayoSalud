package ar.com.mayosalud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.mayosalud.entity.EstudioDetalle;

@Repository
public interface EstudioDetalleRepository extends JpaRepository<EstudioDetalle, Long> {
}

