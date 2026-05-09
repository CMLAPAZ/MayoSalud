package ar.com.mayosalud.config;

import ar.com.mayosalud.entity.Medico;
import ar.com.mayosalud.entity.Paciente;
import ar.com.mayosalud.entity.Turno;
import ar.com.mayosalud.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Intercepta métodos de servicio con AOP para registrar operaciones en auditoría
 * sin acoplar el código de negocio al sistema de trazabilidad.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;

    // ── Médico ──────────────────────────────────────────────────────────────

    @Around("execution(* ar.com.mayosalud.service.MedicoService.guardar(..))")
    public Object auditarMedicoGuardar(ProceedingJoinPoint pjp) throws Throwable {
        Medico medico = (Medico) pjp.getArgs()[0];
        boolean esNuevo = medico.getId() == null;
        Medico resultado = (Medico) pjp.proceed();
        auditoriaService.registrar(
                esNuevo ? "CREAR" : "MODIFICAR",
                "Medico",
                String.valueOf(resultado.getId()),
                resultado.getNombreCompleto() + " | Mat: " + resultado.getMatricula()
        );
        return resultado;
    }

    @AfterReturning("execution(* ar.com.mayosalud.service.MedicoService.darDeBaja(..))")
    public void auditarMedicoBaja(org.aspectj.lang.JoinPoint jp) {
        Long id = (Long) jp.getArgs()[0];
        auditoriaService.registrar("BAJA", "Medico", String.valueOf(id), "Baja lógica");
    }

    // ── Paciente ────────────────────────────────────────────────────────────

    @Around("execution(* ar.com.mayosalud.service.PacienteService.guardar(..))")
    public Object auditarPacienteGuardar(ProceedingJoinPoint pjp) throws Throwable {
        Paciente paciente = (Paciente) pjp.getArgs()[0];
        boolean esNuevo = paciente.getId() == null;
        Paciente resultado = (Paciente) pjp.proceed();
        auditoriaService.registrar(
                esNuevo ? "CREAR" : "MODIFICAR",
                "Paciente",
                String.valueOf(resultado.getId()),
                resultado.getNombreCompleto() + " | DNI: " + resultado.getDni()
        );
        return resultado;
    }

    @AfterReturning("execution(* ar.com.mayosalud.service.PacienteService.darDeBaja(..))")
    public void auditarPacienteBaja(org.aspectj.lang.JoinPoint jp) {
        Long id = (Long) jp.getArgs()[0];
        auditoriaService.registrar("BAJA", "Paciente", String.valueOf(id), "Baja lógica");
    }

    // ── Turno ───────────────────────────────────────────────────────────────

    @Around("execution(* ar.com.mayosalud.service.TurnoService.guardar(..))")
    public Object auditarTurnoGuardar(ProceedingJoinPoint pjp) throws Throwable {
        Turno turno = (Turno) pjp.getArgs()[0];
        boolean esNuevo = turno.getId() == null;
        Turno resultado = (Turno) pjp.proceed();
        auditoriaService.registrar(
                esNuevo ? "CREAR" : "MODIFICAR",
                "Turno",
                String.valueOf(resultado.getId()),
                resultado.getFecha() + " " + resultado.getHora()
                        + " | " + resultado.getPaciente().getNombreCompleto()
                        + " → " + resultado.getMedico().getNombreCompleto()
        );
        return resultado;
    }

    @AfterReturning("execution(* ar.com.mayosalud.service.TurnoService.eliminar(..))")
    public void auditarTurnoEliminar(org.aspectj.lang.JoinPoint jp) {
        Long id = (Long) jp.getArgs()[0];
        auditoriaService.registrar("ELIMINAR", "Turno", String.valueOf(id), "Turno eliminado");
    }

    @AfterReturning("execution(* ar.com.mayosalud.service.TurnoService.cambiarEstado(..))")
    public void auditarTurnoCambioEstado(org.aspectj.lang.JoinPoint jp) {
        Long id = (Long) jp.getArgs()[0];
        String nuevoEstado = jp.getArgs()[1].toString();
        auditoriaService.registrar("MODIFICAR", "Turno", String.valueOf(id),
                "Cambio de estado → " + nuevoEstado);
    }
}
