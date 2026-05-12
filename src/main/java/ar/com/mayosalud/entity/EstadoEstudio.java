package ar.com.mayosalud.entity;

/** Estados posibles de un estudio (etapa 1). */
public enum EstadoEstudio {
    SOLICITADO("Solicitado"),
    REALIZADO("Realizado"),
    INFORMADO("Informado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoEstudio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

