package ar.com.mayosalud.entity;

/** Roles disponibles para los usuarios del sistema. */
public enum RolUsuario {
    ADMIN("Administrador"),
    RECEPCION("Recepción");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
