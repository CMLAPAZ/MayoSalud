package ar.com.mayosalud.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cabecera común de cualquier evento clínico.
 * MVP (Etapa 1): Paciente obligatorio, Turno opcional (nullable), Usuario registrante obligatorio.
 */
@Entity
@Table(name = "eventos_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Turno opcional para cuando el evento nació desde un turno.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id")
    private Turno turno;

    @NotNull(message = "El usuario registrante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioRegistrante;

    @NotNull(message = "La fecha y hora es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @NotNull(message = "El tipo de evento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEventoClinico tipo;

    @Column(length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descripcionGeneral;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}

