package ar.com.mayosalud.entity;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map;

/** Mapeo de DayOfWeek a nombre en español, Lunes–Sábado (sin domingo). */
public final class DiaAtencion {

    public static final Map<DayOfWeek, String> NOMBRES;

    static {
        NOMBRES = new LinkedHashMap<>();
        NOMBRES.put(DayOfWeek.MONDAY,    "Lunes");
        NOMBRES.put(DayOfWeek.TUESDAY,   "Martes");
        NOMBRES.put(DayOfWeek.WEDNESDAY, "Miércoles");
        NOMBRES.put(DayOfWeek.THURSDAY,  "Jueves");
        NOMBRES.put(DayOfWeek.FRIDAY,    "Viernes");
        NOMBRES.put(DayOfWeek.SATURDAY,  "Sábado");
    }

    private DiaAtencion() {}
}
