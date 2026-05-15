package ar.com.mayosalud.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representa un turno médico: relación entre un paciente, un médico, fecha/hora y estado de atención. */
@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotNull(message = "El médico es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoTurno estado = EstadoTurno.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Duración solicitada para el turno (minutos). Valores permitidos: 20/30/45/60
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 20, message = "La duración mínima es 20 minutos")
    @Max(value = 60, message = "La duración máxima es 60 minutos")
    @Column(name = "duracion_minutos", nullable = false)
    @Builder.Default
    private Integer duracionMinutos = 30;

}

