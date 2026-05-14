package ar.com.mayosalud.dto;

import java.util.List;

/** Respuesta JSON para el calendario de turnos libres. */
public record TurnosLibresResponse(List<SlotHorario> slots) {}
