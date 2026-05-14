package ar.com.mayosalud.dto;

/** Representa un slot horario con su estado de disponibilidad. */
public record SlotHorario(String hora, String estado, boolean disponible) {}
