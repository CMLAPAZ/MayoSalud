package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.EstadoTurno;
import ar.com.mayosalud.entity.Turno;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Genera reportes en PDF: agenda diaria y lista de pacientes del día. */
@Service
public class ReporteService {

    private static final String CLINICA = "Clínica Mayo S.R.L.  —  La Paz, Entre Ríos";

    private static final Color C_OSCURO   = new Color(30,  58,  79);
    private static final Color C_AZUL     = new Color(91,  141, 184);
    private static final Color C_FILA_PAR = new Color(244, 247, 250);
    private static final Color C_BORDE    = new Color(226, 232, 240);
    private static final Color C_GRIS     = new Color(108, 117, 125);

    private static final DateTimeFormatter FMT_LARGO =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_TS   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── API pública ───────────────────────────────────────────────────────────

    /** Agenda completa del día: hora, paciente, médico, especialidad, estado y motivo. */
    public byte[] generarAgendaDiaria(LocalDate fecha, List<Turno> turnos) {
        try {
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            agregarFooter(writer);
            doc.open();

            doc.add(buildHeader("AGENDA DEL DÍA", fecha.format(FMT_LARGO)));
            doc.add(Chunk.NEWLINE);

            if (turnos.isEmpty()) {
                Paragraph p = new Paragraph("No hay turnos registrados para este día.", fNormal());
                p.setAlignment(Element.ALIGN_CENTER);
                doc.add(p);
            } else {
                PdfPTable tabla = new PdfPTable(new float[]{45, 115, 110, 95, 68, 90});
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(8);

                for (String h : new String[]{"Hora", "Paciente", "Médico", "Especialidad", "Estado", "Motivo"})
                    celHeader(tabla, h);

                int fila = 0;
                for (Turno t : turnos) {
                    Color bg = fila++ % 2 == 0 ? C_FILA_PAR : Color.WHITE;
                    cel(tabla, t.getHora().format(FMT_HORA),                          bg, Element.ALIGN_CENTER);
                    cel(tabla, t.getPaciente().getNombreCompleto(),                    bg, Element.ALIGN_LEFT);
                    cel(tabla, t.getMedico().getNombreCompleto(),                      bg, Element.ALIGN_LEFT);
                    cel(tabla, t.getMedico().getEspecialidad().getDescripcion(),       bg, Element.ALIGN_LEFT);
                    celEstado(tabla, t.getEstado());
                    cel(tabla, t.getMotivo() != null ? t.getMotivo() : "",            bg, Element.ALIGN_LEFT);
                }
                doc.add(tabla);

                Paragraph tot = new Paragraph(
                        "Total: " + turnos.size() + " turno" + (turnos.size() != 1 ? "s" : ""), fBold());
                tot.setSpacingBefore(10);
                doc.add(tot);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de agenda", e);
        }
    }

    /** Lista simplificada de pacientes del día: nombre, DNI, cobertura y médico. */
    public byte[] generarPacientesDia(LocalDate fecha, List<Turno> turnos) {
        try {
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            agregarFooter(writer);
            doc.open();

            doc.add(buildHeader("PACIENTES DEL DÍA", fecha.format(FMT_LARGO)));
            doc.add(Chunk.NEWLINE);

            if (turnos.isEmpty()) {
                Paragraph p = new Paragraph("No hay pacientes registrados para este día.", fNormal());
                p.setAlignment(Element.ALIGN_CENTER);
                doc.add(p);
            } else {
                PdfPTable tabla = new PdfPTable(new float[]{22, 42, 140, 72, 128, 119});
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(8);

                for (String h : new String[]{"#", "Hora", "Paciente", "DNI", "Cobertura", "Médico"})
                    celHeader(tabla, h);

                int fila = 0;
                for (Turno t : turnos) {
                    Color bg = fila % 2 == 0 ? C_FILA_PAR : Color.WHITE;
                    cel(tabla, String.valueOf(++fila),                                bg, Element.ALIGN_CENTER);
                    cel(tabla, t.getHora().format(FMT_HORA),                         bg, Element.ALIGN_CENTER);
                    cel(tabla, t.getPaciente().getNombreCompleto(),                   bg, Element.ALIGN_LEFT);
                    cel(tabla, t.getPaciente().getDni(),                              bg, Element.ALIGN_CENTER);
                    cel(tabla, t.getPaciente().getObraSocial() != null
                            ? t.getPaciente().getObraSocial().getDescripcion()
                            : "Particular",                                          bg, Element.ALIGN_LEFT);
                    cel(tabla, t.getMedico().getNombreCompleto(),                     bg, Element.ALIGN_LEFT);
                }
                doc.add(tabla);

                Paragraph tot = new Paragraph(
                        "Total: " + turnos.size() + " paciente" + (turnos.size() != 1 ? "s" : ""), fBold());
                tot.setSpacingBefore(10);
                doc.add(tot);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de pacientes", e);
        }
    }

    // ── Constructores internos ────────────────────────────────────────────────

    private PdfPTable buildHeader(String titulo, String subtitulo) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingAfter(4);

        PdfPCell cClinica = new PdfPCell(new Phrase(CLINICA, fHeaderClinica()));
        cClinica.setBackgroundColor(C_OSCURO);
        cClinica.setPadding(8);
        cClinica.setBorder(Rectangle.NO_BORDER);
        t.addCell(cClinica);

        PdfPCell cTitulo = new PdfPCell();
        cTitulo.setBackgroundColor(C_AZUL);
        cTitulo.setPadding(7);
        cTitulo.setBorder(Rectangle.NO_BORDER);
        Phrase ph = new Phrase();
        ph.add(new Chunk(titulo + "\n", fTitulo()));
        ph.add(new Chunk(subtitulo, fSubtitulo()));
        cTitulo.setPhrase(ph);
        t.addCell(cTitulo);

        return t;
    }

    private void celHeader(PdfPTable tabla, String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fHeaderTabla()));
        c.setBackgroundColor(C_OSCURO);
        c.setPaddingTop(5);
        c.setPaddingBottom(5);
        c.setPaddingLeft(4);
        c.setPaddingRight(4);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBorderColor(C_AZUL);
        tabla.addCell(c);
    }

    private void cel(PdfPTable tabla, String texto, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", fNormal()));
        c.setBackgroundColor(bg);
        c.setPadding(4);
        c.setHorizontalAlignment(align);
        c.setBorderColor(C_BORDE);
        tabla.addCell(c);
    }

    private void celEstado(PdfPTable tabla, EstadoTurno estado) {
        Color bg = switch (estado) {
            case PENDIENTE  -> new Color(255, 193, 7);
            case CONFIRMADO -> C_AZUL;
            case ATENDIDO   -> new Color(25, 135, 84);
            case CANCELADO  -> new Color(220, 53, 69);
            case AUSENTE    -> C_GRIS;
        };
        Color textColor = estado == EstadoTurno.PENDIENTE ? C_OSCURO : Color.WHITE;
        Font f = new Font(bfBold(), 7.5f, Font.BOLD, textColor);
        PdfPCell c = new PdfPCell(new Phrase(estado.getDescripcion(), f));
        c.setBackgroundColor(bg);
        c.setPadding(4);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBorderColor(C_BORDE);
        tabla.addCell(c);
    }

    private void agregarFooter(PdfWriter writer) {
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter w, Document d) {
                PdfContentByte cb = w.getDirectContent();
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                        new Phrase("Generado: " + LocalDateTime.now().format(FMT_TS), fSmall()),
                        d.left(), d.bottom() - 14, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                        new Phrase("Pág. " + w.getPageNumber(), fSmall()),
                        d.right(), d.bottom() - 14, 0);
            }
        });
    }

    // ── Fonts ─────────────────────────────────────────────────────────────────

    private static BaseFont bf() {
        try { return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    private static BaseFont bfBold() {
        try { return BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private Font fHeaderClinica() { return new Font(bfBold(), 10,   Font.BOLD,   Color.WHITE);  }
    private Font fTitulo()        { return new Font(bfBold(), 13,   Font.BOLD,   Color.WHITE);  }
    private Font fSubtitulo()     { return new Font(bf(),     8.5f, Font.NORMAL, Color.WHITE);  }
    private Font fHeaderTabla()   { return new Font(bfBold(), 7.5f, Font.BOLD,   Color.WHITE);  }
    private Font fNormal()        { return new Font(bf(),     8,    Font.NORMAL, C_OSCURO);     }
    private Font fBold()          { return new Font(bfBold(), 8.5f, Font.BOLD,   C_OSCURO);     }
    private Font fSmall()         { return new Font(bf(),     6.5f, Font.NORMAL, C_GRIS);       }
}
