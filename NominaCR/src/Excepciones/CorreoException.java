package Excepciones;

/**
 * Se lanza cuando ocurre un error al enviar un correo (sin conexión,
 * credenciales inválidas, dirección de destino inexistente o mal
 * formada, adjunto faltante, etc.).
 */
public class CorreoException extends Exception {

    public CorreoException(String mensaje) {
        super(mensaje);
    }

    public CorreoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
