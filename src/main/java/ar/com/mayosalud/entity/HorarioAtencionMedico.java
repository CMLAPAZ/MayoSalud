package ar.com.mayosalud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_atencion_medico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtencionMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    @NotNull(message = "El médico es obligatorio")
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    @NotNull(message = "El día de semana es obligatorio")
    private DayOfWeek diaSemana;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_desde", nullable = false)
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaDesde;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_hasta", nullable = false)
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaHasta;

    @Column(name = "duracion_base_minutos", nullable = false)
    private int duracionBaseMinutos = 30;

    @Column(nullable = false)
    private boolean activo = true;
}
