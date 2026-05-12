package ar.com.mayosalud.entity;

/** Obras sociales y prepagas aceptadas por la clínica. PARTICULAR cubre pacientes sin cobertura. */
public enum ObraSocial {
    PARTICULAR("Particular / Sin obra social"),
    PAMI("PAMI"),
    IOSPER("OSER / IOSPER"),
    IOMA("IOMA"),
    OSDE("OSDE"),
    SWISS_MEDICAL("Swiss Medical"),
    GALENO("Galeno"),
    MEDIFE("Medifé"),
    OMINT("OMINT"),
    SANCOR_SALUD("Sancor Salud"),
    FEDERADA("Federada Salud"),
    AVALIAN("Avalian"),
    PREVENCION_SALUD("Prevención Salud"),
    JERARQUICOS("Jerárquicos Salud"),
    UNION_PERSONAL("Unión Personal"),
    OSECAC("OSECAC"),
    OSPE("OSPE"),
    OSDEPYM("OSDEPYM"),
    OSPRERA("OSPRERA"),
    OSPACA("OSPACA"),
    OSPAT("OSPAT"),
    OSUTHGRA("OSUTHGRA"),
    OSPEDYC("OSPEDYC"),
    OSSEG("OSSEG"),
    OSFATUN("OSFATUN"),
    OSMATA("OSMATA"),
    OSUOMRA("OSUOMRA / UOM"),
    IOSFA("IOSFA"),
    DASUTEN("DASUTEN"),
    OSFATLYF("OSFATLYF (Luz y Fuerza)"),
    ACA_SALUD("ACA Salud"),
    AMUR("AMUR"),
    APROSS("APROSS");

    private final String descripcion;

    ObraSocial(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
