package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.Feriado;
import ar.com.mayosalud.entity.TipoFeriado;
import ar.com.mayosalud.repository.FeriadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Gestión de feriados nacionales, provinciales y locales. */
@Service
@RequiredArgsConstructor
@Transactional
public class FeriadoService {

    private final FeriadoRepository feriadoRepository;

    @Transactional(readOnly = true)
    public List<Feriado> listarTodos() {
        return feriadoRepository.findAllByOrderByFechaAsc();
    }

    @Transactional(readOnly = true)
    public List<Feriado> listarAnio(int anio) {
        return feriadoRepository.findByFechaBetweenOrderByFechaAsc(
                LocalDate.of(anio, 1, 1), LocalDate.of(anio, 12, 31));
    }

    @Transactional(readOnly = true)
    public Optional<Feriado> buscarEnFecha(LocalDate fecha) {
        return feriadoRepository.findByFechaAndActivoTrue(fecha);
    }

    @Transactional(readOnly = true)
    public boolean esFeriado(LocalDate fecha) {
        return feriadoRepository.existsByFechaAndActivoTrue(fecha);
    }

    @Transactional(readOnly = true)
    public Feriado buscarPorId(Long id) {
        return feriadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feriado no encontrado"));
    }

    public Feriado guardar(Feriado feriado) {
        return feriadoRepository.save(feriado);
    }

    public void eliminar(Long id) {
        feriadoRepository.deleteById(id);
    }

    /** Carga los feriados predefinidos y asegura que los feriados locales conocidos estén presentes. */
    public void inicializarSiVacio() {
        if (feriadoRepository.count() == 0) {
            List<Feriado> feriados = List.of(
                // ── Locales La Paz ────────────────────────────────────────────
                f(2026,  1, 24, "Ntra. Señora de La Paz — Patrona de la Ciudad", TipoFeriado.LOCAL),
                f(2026,  7, 13, "Día de la Ciudad de La Paz",                    TipoFeriado.LOCAL),

                // ── Provinciales Entre Ríos ───────────────────────────────────
                f(2026,  2,  3, "Batalla de Caseros",                            TipoFeriado.PROVINCIAL),
                f(2026,  6, 27, "Día del Empleado del Estado de Entre Ríos",     TipoFeriado.PROVINCIAL),

                // ── Nacionales ────────────────────────────────────────────────
                f(2026,  1,  1, "Año Nuevo",                                     TipoFeriado.NACIONAL),
                f(2026,  2, 16, "Carnaval",                                      TipoFeriado.NACIONAL),
                f(2026,  2, 17, "Carnaval",                                      TipoFeriado.NACIONAL),
                f(2026,  3, 24, "Día Nacional de la Memoria por la Verdad y la Justicia", TipoFeriado.NACIONAL),
                f(2026,  4,  2, "Día del Veterano y de los Caídos en Malvinas",  TipoFeriado.NACIONAL),
                f(2026,  4,  3, "Viernes Santo",                                 TipoFeriado.NACIONAL),
                f(2026,  5,  1, "Día del Trabajador",                            TipoFeriado.NACIONAL),
                f(2026,  5, 25, "Revolución de Mayo",                            TipoFeriado.NACIONAL),
                f(2026,  6, 17, "Paso a la Inmortalidad del Gral. Güemes",       TipoFeriado.NACIONAL),
                f(2026,  6, 20, "Paso a la Inmortalidad del Gral. Belgrano — Día de la Bandera", TipoFeriado.NACIONAL),
                f(2026,  7,  9, "Día de la Independencia",                       TipoFeriado.NACIONAL),
                f(2026,  8, 17, "Paso a la Inmortalidad del Gral. San Martín",   TipoFeriado.NACIONAL),
                f(2026, 10, 12, "Día del Respeto a la Diversidad Cultural",      TipoFeriado.NACIONAL),
                f(2026, 11, 20, "Día de la Soberanía Nacional",                  TipoFeriado.NACIONAL),
                f(2026, 12,  8, "Inmaculada Concepción de María",                TipoFeriado.NACIONAL),
                f(2026, 12, 25, "Navidad",                                       TipoFeriado.NACIONAL)
            );
            feriadoRepository.saveAll(feriados);
        } else {
            // Asegura feriados locales que se agregaron después de la carga inicial
            asegurarFeriado(2026, 7, 13, "Día de la Ciudad de La Paz", TipoFeriado.LOCAL);
        }
    }

    private void asegurarFeriado(int anio, int mes, int dia, String nombre, TipoFeriado tipo) {
        LocalDate fecha = LocalDate.of(anio, mes, dia);
        if (!feriadoRepository.existsByFecha(fecha)) {
            feriadoRepository.save(f(anio, mes, dia, nombre, tipo));
        }
    }

    private Feriado f(int anio, int mes, int dia, String nombre, TipoFeriado tipo) {
        return Feriado.builder()
                .fecha(LocalDate.of(anio, mes, dia))
                .nombre(nombre)
                .tipo(tipo)
                .build();
    }
}
