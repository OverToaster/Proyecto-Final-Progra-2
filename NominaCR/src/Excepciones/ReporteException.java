package Excepciones;

/**
 * Se lanza cuando ocurre un error al generar un reporte PDF (ruta
 * inválida, error de la biblioteca iText, etc.).
 */
public class ReporteException extends Exception {

    public ReporteException(String mensaje) {
        super(mensaje);
    }

    public ReporteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
