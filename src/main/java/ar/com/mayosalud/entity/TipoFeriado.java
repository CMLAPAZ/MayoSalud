package ar.com.mayosalud.entity;

/** Ámbito geográfico de aplicación del feriado. */
public enum TipoFeriado {
    NACIONAL("Nacional"),
    PROVINCIAL("Provincial — Entre Ríos"),
    LOCAL("Local — La Paz");

    private final String descripcion;

    TipoFeriado(String descripcion) { this.descripcion = descripcion; }

    public String getDescripcion() { return descripcion; }
}
