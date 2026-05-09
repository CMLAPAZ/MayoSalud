package ar.com.mayosalud.entity;

public enum Especialidad {
    CLINICA_MEDICA("Clínica Médica"),
    MEDICINA_GENERAL("Medicina General"),
    PEDIATRIA("Pediatría"),
    GINECOLOGIA("Ginecología"),
    OBSTETRICIA("Obstetricia"),
    CARDIOLOGIA("Cardiología"),
    TRAUMATOLOGIA("Traumatología y Ortopedia"),
    CIRUGIA_GENERAL("Cirugía General"),
    CIRUGIA_VASCULAR("Cirugía Vascular"),
    FLEBOLOGIA("Flebología"),
    GASTROENTEROLOGIA("Gastroenterología"),
    HEPATOLOGIA("Hepatología"),
    NEUROLOGIA("Neurología"),
    DERMATOLOGIA("Dermatología"),
    ENDOCRINOLOGIA("Endocrinología"),
    DIABETOLOGIA("Diabetología"),
    NEUMONOLOGIA("Neumonología"),
    UROLOGIA("Urología"),
    OTORRINOLARINGOLOGIA("Otorrinolaringología"),
    OFTALMOLOGIA("Oftalmología"),
    REUMATOLOGIA("Reumatología"),
    INFECTOLOGIA("Infectología"),
    PSICOLOGIA("Psicología"),
    PSIQUIATRIA("Psiquiatría"),
    NUTRICION("Nutrición"),
    KINESIOLOGIA("Kinesiología y Rehabilitación"),
    DIAGNOSTICO_IMAGENES("Diagnóstico por Imágenes"),
    ECOGRAFIA("Ecografía"),
    LABORATORIO("Laboratorio de Análisis Clínicos"),
    ENFERMERIA("Enfermería Profesional"),
    GUARDIA("Guardia Médica");

    private final String descripcion;

    Especialidad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
