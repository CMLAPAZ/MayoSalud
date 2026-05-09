package ar.com.mayosalud.entity;

/** Estados posibles de un turno a lo largo de su ciclo de vida. */
public enum EstadoTurno {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    ATENDIDO("Atendido"),
    CANCELADO("Cancelado"),
    AUSENTE("Ausente");

    private final String descripcion;

    EstadoTurno(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
