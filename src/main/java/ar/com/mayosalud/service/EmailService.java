package ar.com.mayosalud.service;

import ar.com.mayosalud.entity.Turno;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Envía emails HTML institucionales a los pacientes. */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${recordatorio.remitente:}")
    private String remitente;

    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    private static final DateTimeFormatter FMT_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    /** Envía el recordatorio de turno al paciente. Retorna true si se envió correctamente. */
    public boolean enviarRecordatorio(Turno turno) {
        String destino = turno.getPaciente().getEmail();
        if (destino == null || destino.isBlank()) return false;

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destino);
            helper.setSubject("Recordatorio de turno — Clínica Mayo");
            helper.setText(buildHtml(turno), true);
            mailSender.send(msg);
            return true;
        } catch (Exception e) {
            log.error("Error enviando recordatorio a {}: {}", destino, e.getMessage());
            return false;
        }
    }

    private String buildHtml(Turno t) {
        String nombrePaciente = t.getPaciente().getNombreCompleto();
        String fecha  = t.getFecha().format(FMT_FECHA);
        String hora   = t.getHora().format(FMT_HORA);
        String medico = t.getMedico().getNombreCompleto();
        String espec  = t.getMedico().getEspecialidad().getDescripcion();
        String motivo = (t.getMotivo() != null && !t.getMotivo().isBlank())
                ? "<p style='margin:6px 0 0;'><strong>Motivo:</strong> " + escHtml(t.getMotivo()) + "</p>"
                : "";

        return """
            <!DOCTYPE html>
            <html lang="es">
            <body style="margin:0;padding:20px;background:#F4F7FA;font-family:Arial,sans-serif;">
            <div style="max-width:520px;margin:0 auto;background:#fff;border-radius:12px;
                        overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,.1);">

              <!-- Encabezado -->
              <div style="background:#1E3A4F;padding:22px 24px;text-align:center;">
                <p style="color:#fff;font-size:20px;font-weight:700;margin:0;">Clínica Mayo S.R.L.</p>
                <p style="color:rgba(255,255,255,.65);font-size:13px;margin:4px 0 0;">
                  La Paz, Entre Ríos
                </p>
              </div>

              <!-- Cuerpo -->
              <div style="padding:28px 28px 20px;">
                <h2 style="color:#1E3A4F;margin:0 0 16px;font-size:18px;">
                  Recordatorio de turno
                </h2>
                <p style="color:#374151;margin:0 0 12px;">
                  Hola, <strong>""" + escHtml(nombrePaciente) + """
                </strong>.</p>
                <p style="color:#374151;margin:0 0 18px;">
                  Le recordamos que tiene un turno programado para <strong>mañana</strong>:
                </p>

                <!-- Detalle del turno -->
                <div style="background:#F4F7FA;border-left:4px solid #5B8DB8;border-radius:8px;
                            padding:16px 18px;margin-bottom:20px;">
                  <p style="margin:0 0 8px;color:#1E3A4F;">
                    <strong>&#128197; Fecha:</strong> """ + escHtml(fecha) + """
                  </p>
                  <p style="margin:0 0 8px;color:#1E3A4F;">
                    <strong>&#128336; Hora:</strong> """ + hora + """
                     hs
                  </p>
                  <p style="margin:0 0 8px;color:#1E3A4F;">
                    <strong>&#128104;&#8205;&#9877;&#65039; Médico:</strong> """ + escHtml(medico) + """
                  </p>
                  <p style="margin:0;color:#1E3A4F;">
                    <strong>&#127973; Especialidad:</strong> """ + escHtml(espec) + """
                  </p>
                  """ + motivo + """
                </div>

                <p style="color:#6B7280;font-size:13px;margin:0;">
                  Si necesita cancelar o reprogramar, comuníquese con nosotros con anticipación.
                </p>
              </div>

              <!-- Pie -->
              <div style="background:#F4F7FA;border-top:1px solid #E5E7EB;
                          padding:14px 24px;text-align:center;">
                <p style="color:#9CA3AF;font-size:11px;margin:0;line-height:1.6;">
                  Clínica Mayo S.R.L. — La Paz, Entre Ríos<br>
                  Este es un mensaje automático, por favor no responda este correo.
                </p>
              </div>

            </div>
            </body>
            </html>
            """;
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
