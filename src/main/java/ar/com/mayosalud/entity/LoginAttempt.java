package ar.com.mayosalud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/** Estado persistente de intentos fallidos para conservar bloqueos tras reinicios. */
@Entity
@Table(name = "login_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private int intentosFallidos;

    private LocalDateTime bloqueadoDesde;

    @Column(nullable = false)
    private LocalDateTime actualizado;
}
