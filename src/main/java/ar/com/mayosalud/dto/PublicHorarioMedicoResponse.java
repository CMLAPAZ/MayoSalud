package ar.com.mayosalud.dto;

/** Horario publico de atencion de un medico. */
public record PublicHorarioMedicoResponse(
        String diaSemana,
        String horaDesde,
        String horaHasta,
        int duracionBaseMinutos
) {}
