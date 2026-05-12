package ar.com.mayosalud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Detalle de evolución médica asociado a un EventoClinico. */
@Entity
@Table(name = "evolucion_medica_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolucionMedicaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El evento clínico es obligatorio")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_clinico_id", nullable = false, unique = true)
    private EventoClinico evento;

    @Column(length = 150)
    private String motivoConsulta;

    @Column(columnDefinition = "TEXT")
    private String evolucionTexto;

    @Column(columnDefinition = "TEXT")
    private String examenFisico;

    @Column(columnDefinition = "TEXT")
    private String impresionDiagnostica;

    @Column(columnDefinition = "TEXT")
    private String conducta;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;
}

