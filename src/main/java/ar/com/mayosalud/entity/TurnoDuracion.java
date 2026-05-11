package ar.com.mayosalud.entity;

/** Helper para validar duración permitida para turnos (15/30/45/60). */
public final class TurnoDuracion {
    public static final int[] PERMITIDAS = {15, 30, 45, 60};

    private TurnoDuracion() {}

    public static boolean esPermitida(int minutos) {
        for (int v : PERMITIDAS) {
            if (v == minutos) return true;
        }
        return false;
    }
}

