package ar.com.mayosalud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/** Detalle de estudio asociado a un EventoClinico. */
@Entity
@Table(name = "estudio_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El evento clínico es obligatorio")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_clinico_id", nullable = false, unique = true)
    private EventoClinico evento;

    @Column(length = 80)
    private String tipoEstudio;

    @Column(length = 180)
    private String nombreEstudio;

    @Column(columnDefinition = "TEXT")
    private String indicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEstudio estado;

    private java.time.LocalDate fechaSolicitud;
    private java.time.LocalDate fechaRealizacion;

    @Column(columnDefinition = "TEXT")
    private String resultadoTexto;

    // Opcional futuro (simple string, etapa 1 lo dejamos pero no implementamos UI de archivos).
    @Column(length = 500)
    private String resultadoUrl;
}

