package ar.com.mayosalud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Representa un paciente de la clínica con sus datos personales, cobertura médica e historial clínico. */
@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

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

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 10)
    private String sexo;

    @Column(length = 200)
    private String direccion;

    @Pattern(regexp = "^[0-9]*$", message = "El teléfono debe contener solo números")
    @Column(length = 30)
    private String telefono;

    @Email(message = "El email no es válido")
    @Column(length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private ObraSocial obraSocial = ObraSocial.PARTICULAR;

    @Column(length = 50)
    private String nroAfiliado;

    @Column(columnDefinition = "TEXT")
    private String antecedentes;

    @Column
    private LocalDateTime consentimientoFecha;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Turno> turnos;

    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }
}
