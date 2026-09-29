package iTicket.Douglas.Auth.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destino, String token, String nombreUsuario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            // URL del Frontend
            String urlRecuperacion = "http://127.0.0.1:5502/nuevaContrasena.html?token=" + token;

            String htmlMsg = "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <meta name=\"color-scheme\" content=\"light\">\n" +
                    "  <title>Recuperación de contraseña</title>\n" +
                    "</head>\n" +
                    "<body style=\"margin:0; padding:0; background-color:#0e1b4d; font-family:'Segoe UI', Roboto, Helvetica, Arial, sans-serif; -webkit-font-smoothing:antialiased;\">\n" +
                    "\n" +
                    "  <div style=\"display:none; max-height:0; overflow:hidden; opacity:0; font-size:1px; line-height:1px; color:#0e1b4d;\">\n" +
                    "    Crea tu nueva contraseña con un clic. El enlace expira en 30 minutos.\n" +
                    "  </div>\n" +
                    "\n" +
                    "  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color:#0e1b4d; background-image:linear-gradient(160deg,#0e1b4d,#152a6b);\">\n" +
                    "    <tr>\n" +
                    "      <td align=\"center\" style=\"padding:48px 16px;\">\n" +
                    "\n" +
                    "        <table role=\"presentation\" width=\"560\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                    "               style=\"width:100%; max-width:560px; background-color:#ffffff; border-radius:20px; overflow:hidden; box-shadow:0 12px 40px rgba(0,0,0,0.25);\">\n" +
                    "\n" +
                    "          <!-- Encabezado con logo -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:32px 32px 12px 32px;\">\n" +
                    "              <img src=\"logo.png\" alt=\"iTicket\" width=\"56\" height=\"49\" style=\"display:block; margin:0 auto; border:0; outline:none;\">\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:4px 44px 0 44px;\">\n" +
                    "              <h1 style=\"margin:0; font-size:24px; line-height:1.35; font-weight:800; color:#0e1b4d; text-align:center;\">\n" +
                    "                ¿Olvidaste tu contraseña?\n" +
                    "              </h1>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Caja azul marino con el mensaje principal -->\n" +
                    "          <tr>\n" +
                    "            <td style=\"padding:24px 40px 0 40px;\">\n" +
                    "              <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                    "                     style=\"background-color:#152a6b; border-radius:14px;\">\n" +
                    "                <tr>\n" +
                    "                  <td align=\"center\" style=\"padding:20px 24px; font-size:14px; line-height:1.7; color:#ffffff; text-align:center;\">\n" +
                    "                    Hola <strong>" + nombreUsuario + "</strong>, recibimos una solicitud para restablecer tu contraseña.\n" +
                    "                    Haz clic en el botón para crear una nueva.\n" +
                    "                  </td>\n" +
                    "                </tr>\n" +
                    "              </table>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Botón mágico -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:28px 40px 0 40px;\">\n" +
                    "              <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                    "                <tr>\n" +
                    "                  <td align=\"center\" bgcolor=\"#4f9cf0\" style=\"border-radius:10px; background-color:#4f9cf0;\">\n" +
                    "                    <a href=\"" + urlRecuperacion + "\" target=\"_blank\"\n" +
                    "                       style=\"display:inline-block; padding:15px 40px; font-size:15px; font-weight:700; color:#ffffff; text-decoration:none; border-radius:10px; letter-spacing:0.3px;\">\n" +
                    "                      Restablecer contraseña\n" +
                    "                    </a>\n" +
                    "                  </td>\n" +
                    "                </tr>\n" +
                    "              </table>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Nota de expiración -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:22px 40px 0 40px; color:#5b6b9a; font-size:13px; line-height:1.6; text-align:center;\">\n" +
                    "              Por seguridad, este enlace es de <strong style=\"color:#0e1b4d;\">un solo uso</strong> y expira en\n" +
                    "              <strong style=\"color:#0e1b4d;\">30 minutos</strong>.\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:14px 40px 0 40px; color:#94a3c4; font-size:13px; line-height:1.7; text-align:center;\">\n" +
                    "              Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña actual seguirá funcionando.\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Separador -->\n" +
                    "          <tr>\n" +
                    "            <td style=\"padding:26px 40px 0 40px;\">\n" +
                    "              <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                    "                <tr><td height=\"1\" style=\"height:1px; font-size:0; line-height:0; background-color:#e5e9f5;\">&nbsp;</td></tr>\n" +
                    "              </table>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Enlace alternativo -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:18px 40px 32px 40px; color:#94a3c4; font-size:12px; line-height:1.6; text-align:center;\">\n" +
                    "              <p style=\"margin:0 0 6px 0;\">Si el botón no funciona, copia y pega este enlace en tu navegador:</p>\n" +
                    "              <p style=\"margin:0; word-break:break-all;\">\n" +
                    "                <a href=\"" + urlRecuperacion + "\" style=\"color:#4f9cf0; text-decoration:underline;\">" + urlRecuperacion + "</a>\n" +
                    "              </p>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "        </table>\n" +
                    "\n" +
                    "        <table role=\"presentation\" width=\"560\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%; max-width:560px;\">\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:24px 16px 0 16px; color:#8b9ac9; font-size:12px; line-height:1.7;\">\n" +
                    "              Este es un correo automático, por favor no respondas.<br>\n" +
                    "              &copy; iTicket &middot; Sistema de gestión de tickets\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "\n" +
                    "      </td>\n" +
                    "    </tr>\n" +
                    "  </table>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";

            helper.setTo(destino);
            helper.setSubject(" Restablece tu contraseña de iTicket");
            helper.setText(htmlMsg, true);

            mailSender.send(mensaje);
            log.info("Correo de recuperación enviado exitosamente a: " + destino);

        } catch (MessagingException e) {
            log.error("Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }
}