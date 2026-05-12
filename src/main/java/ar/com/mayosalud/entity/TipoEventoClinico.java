package ar.com.mayosalud.entity;

/** Tipos de eventos clínicos en la historia clínica. */
public enum TipoEventoClinico {
    SIGNOS_VITALES("Signos vitales"),
    EVOLUCION_MEDICA("Evolución médica"),
    ESTUDIO("Estudio");

    private final String descripcion;

    TipoEventoClinico(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

