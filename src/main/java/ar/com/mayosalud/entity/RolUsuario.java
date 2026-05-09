package ar.com.mayosalud.entity;

/** Roles disponibles para los usuarios del sistema. */
public enum RolUsuario {
    ADMIN("Administrador"),
    RECEPCION("Recepción"),
    MEDICO("Médico"),
    ENFERMERIA("Enfermería");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
