package iTicket.Douglas.Auth.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public void enviarCorreoRecuperacion(String destino, String codigo, String nombreUsuario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            String htmlMsg = "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <meta name=\"color-scheme\" content=\"light\">\n" +
                    "  <title>Recuperacion de contrasena</title>\n" +
                    "</head>\n" +
                    "<body style=\"margin:0; padding:0; background-color:#0e1b4d; font-family:'Segoe UI', Roboto, Helvetica, Arial, sans-serif; -webkit-font-smoothing:antialiased;\">\n" +
                    "\n" +
                    "  <div style=\"display:none; max-height:0; overflow:hidden; opacity:0; font-size:1px; line-height:1px; color:#0e1b4d;\">\n" +
                    "    Tu codigo de recuperacion de 6 digitos.\n" +
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
                    "              <img src=\"https://res.cloudinary.com/jal7yrma/image/upload/v1790796517/Logo.png\" alt=\"iTicket\" width=\"56\" height=\"49\" style=\"display:block; margin:0 auto; border:0; outline:none;\">\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:4px 44px 0 44px;\">\n" +
                    "              <h1 style=\"margin:0; font-size:24px; line-height:1.35; font-weight:800; color:#0e1b4d; text-align:center;\">\n" +
                    "                Olvidaste tu contrasena?\n" +
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
                    "                    Hola <strong>" + nombreUsuario + "</strong>, recibimos una solicitud para restablecer tu contrasena.\n" +
                    "                    Utiliza este codigo de validacion para continuar:\n" +
                    "                  </td>\n" +
                    "                </tr>\n" +
                    "              </table>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Codigo -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:28px 40px 0 40px;\">\n" +
                    "              <h2 style=\"font-size:42px; letter-spacing:12px; color:#4f9cf0; margin:0; font-weight:bold;\">" + codigo + "</h2>\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <!-- Nota de expiracion -->\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:22px 40px 0 40px; color:#5b6b9a; font-size:13px; line-height:1.6; text-align:center;\">\n" +
                    "              Por seguridad, este codigo es de <strong style=\"color:#0e1b4d;\">un solo uso</strong> y expira en\n" +
                    "              <strong style=\"color:#0e1b4d;\">30 minutos</strong>.\n" +
                    "            </td>\n" +
                    "          </tr>\n" +
                    "\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:14px 40px 0 40px; color:#94a3c4; font-size:13px; line-height:1.7; text-align:center;\">\n" +
                    "              Si no solicitaste este cambio, puedes ignorar este correo. Tu contrasena actual seguira funcionando.\n" +
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
                    "          </table>\n" +
                    "\n" +
                    "        <table role=\"presentation\" width=\"560\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%; max-width:560px;\">\n" +
                    "          <tr>\n" +
                    "            <td align=\"center\" style=\"padding:24px 16px 0 16px; color:#8b9ac9; font-size:12px; line-height:1.7;\">\n" +
                    "              Este es un correo automatico, por favor no respondas.<br>\n" +
                    "              &copy; iTicket &middot; Sistema de gestion de tickets\n" +
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

            helper.setFrom(correoRemitente);
            helper.setTo(destino);
            helper.setSubject("Restablece tu contrasena de iTicket");
            helper.setText(htmlMsg, true);

            mailSender.send(mensaje);
            log.info("Correo de recuperacion enviado exitosamente a: " + destino);

        } catch (MessagingException e) {
            log.error("Error al enviar el correo de recuperacion: " + e.getMessage());
        }
    }
}
