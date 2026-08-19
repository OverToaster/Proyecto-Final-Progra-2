package Correo;

import Excepciones.CorreoException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Servicio de envío de correos con adjunto PDF vía JavaMail (Etapa 5). La
 * configuración de la cuenta (correo remitente, clave, servidor SMTP) se
 * recibe por constructor para no dejar credenciales quemadas dentro de la
 * lógica de envío; los valores reales viven en
 * {@link Utilidades.ConfiguracionCorreo}.
 */
public class ServicioCorreo {

    private final String correoRemitente;
    private final String claveAplicacion;
    private final String servidorSmtp;
    private final int puertoSmtp;

    public ServicioCorreo(String correoRemitente, String claveAplicacion, String servidorSmtp, int puertoSmtp) {
        this.correoRemitente = correoRemitente;
        this.claveAplicacion = claveAplicacion;
        this.servidorSmtp = servidorSmtp;
        this.puertoSmtp = puertoSmtp;
    }

    /**
     * Envía un correo con un archivo adjunto.
     *
     * @throws CorreoException si no hay conexión, las credenciales son
     * inválidas, la dirección de destino no existe/es inválida, o el
     * archivo adjunto no se puede leer.
     */
    public void enviarConAdjunto(String correoDestino, String asunto, String cuerpo, File adjunto)
            throws CorreoException {

        if (correoDestino == null || correoDestino.trim().isEmpty()) {
            throw new CorreoException("El correo de destino no puede estar vacío.");
        }
        if (adjunto == null || !adjunto.exists()) {
            throw new CorreoException("El archivo adjunto no existe: " + adjunto);
        }

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", servidorSmtp);
        propiedades.put("mail.smtp.port", String.valueOf(puertoSmtp));

        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correoRemitente, claveAplicacion);
            }
        });

        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(correoRemitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            mensaje.setSubject(asunto);

            MimeBodyPart parteTexto = new MimeBodyPart();
            parteTexto.setText(cuerpo);

            MimeBodyPart parteAdjunto = new MimeBodyPart();
            parteAdjunto.attachFile(adjunto);

            MimeMultipart contenido = new MimeMultipart();
            contenido.addBodyPart(parteTexto);
            contenido.addBodyPart(parteAdjunto);
            mensaje.setContent(contenido);

            Transport.send(mensaje);

        } catch (MessagingException | IOException e) {
            throw new CorreoException("No se pudo enviar el correo a " + correoDestino
                    + ". Verifique la conexión a internet y las credenciales configuradas en "
                    + "Utilidades.ConfiguracionCorreo.", e);
        }
    }
}
