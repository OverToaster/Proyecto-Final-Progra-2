package Excepciones;

import java.io.IOException;

/**
 * Se lanza cuando ocurre un error de lectura/escritura sobre un archivo de
 * datos, o cuando el contenido de una línea no tiene el formato esperado
 * (campos faltantes, tipos inválidos, id corrupto, etc.).
 *
 * Extiende IOException a propósito: así puede propagarse a través de los
 * métodos de {@link AccesoDatos.contratos.IRepositorio} sin tener que
 * ampliar esa interfaz con una nueva cláusula throws.
 */
public class ArchivoInvalidoException extends IOException {

    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public ArchivoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
