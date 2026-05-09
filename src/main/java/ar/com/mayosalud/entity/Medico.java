package ar.com.mayosalud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/** Representa un médico de la clínica con sus datos profesionales y especialidad. Usa soft delete (activo). */
@Entity
@Table(name = "medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El apellido solo puede contener letras")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "El DNI debe tener 7 u 8 dígitos numéricos")
    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "^[0-9]+$", message = "La matrícula debe contener solo números")
    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @NotNull(message = "La especialidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Especialidad especialidad;

    @Email(message = "El email no es válido")
    @Column(length = 150)
    private String email;

    @Pattern(regexp = "^[0-9]*$", message = "El teléfono debe contener solo números")
    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Turno> turnos;

    public String getNombreCompleto() {
        return "Dr/a. " + apellido + ", " + nombre;
    }
}
