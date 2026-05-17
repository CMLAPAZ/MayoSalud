package ar.com.mayosalud.dto;

/** Datos publicos de un medico para integraciones externas. */
public record PublicMedicoResponse(
        Long id,
        String nombreCompleto,
        String especialidad
) {}
