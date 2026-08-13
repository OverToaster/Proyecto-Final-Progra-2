package Utilidades;

/**
 * Configuración de la cuenta de correo usada para enviar los reportes
 * (Etapa 5).
 *
 * IMPORTANTE: reemplace estos valores por los de una cuenta de prueba
 * real antes de ejecutar el envío de correos. Para Gmail, CORREO_CLAVE
 * debe ser una "contraseña de aplicación" (no la contraseña normal de la
 * cuenta) generada desde myaccount.google.com/apppasswords, ya que Gmail
 * bloquea el login directo de aplicaciones de escritorio con la
 * contraseña normal.
 */
public final class ConfiguracionCorreo {

    public static final String CORREO_REMITENTE = "adrianosb2008@gmail.com";
    public static final String CORREO_CLAVE = "hkwu hdjb pggi dedu";
    public static final String SERVIDOR_SMTP = "smtp.gmail.com";
    public static final int PUERTO_SMTP = 587;

    public static final String CORREO_PATRONO = "adrianosb2008@gmail.com";

    private ConfiguracionCorreo() {
        // clase de configuración: no se instancia
    }
}
