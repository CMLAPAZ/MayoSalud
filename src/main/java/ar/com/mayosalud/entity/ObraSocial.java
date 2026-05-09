package ar.com.mayosalud.entity;

/** Obras sociales y prepagas aceptadas por la clínica. PARTICULAR cubre pacientes sin cobertura. */
public enum ObraSocial {
    OSDE("OSDE"),
    SWISS_MEDICAL("Swiss Medical"),
    GALENO("Galeno"),
    MEDICUS("Medicus"),
    OMINT("OMINT"),
    IOMA("IOMA"),
    PAMI("PAMI"),
    ACCORD("Accord Salud"),
    PARTICULAR("Particular / Sin obra social");

    private final String descripcion;

    ObraSocial(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
