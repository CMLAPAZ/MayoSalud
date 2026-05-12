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

/** Detalle de signos vitales asociado a un EventoClinico. */
@Entity
@Table(name = "signos_vitales_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignosVitalesDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El evento clínico es obligatorio")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_clinico_id", nullable = false, unique = true)
    private EventoClinico evento;

    private Double temperatura;

    private Integer presionSistolica;
    private Integer presionDiastolica;

    private Integer pulsoFrecuencia;

    private Double saturacionOxigeno;

    private Double peso;
    private Double talla;

    @Column(columnDefinition = "TEXT")
    private String observacionesEspecificas;
}

