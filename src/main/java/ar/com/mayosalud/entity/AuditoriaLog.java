package ar.com.mayosalud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/** Registro inmutable de una operación realizada en el sistema (quién, qué, cuándo, desde dónde). */
@Entity
@Table(name = "auditoria_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 20)
    private String accion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(length = 20)
    private String entidadId;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 50)
    private String ip;
}
