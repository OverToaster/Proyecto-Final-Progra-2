package Excepciones;

/**
 * Se lanza cuando los datos de una entidad no cumplen las reglas de negocio
 * mínimas antes de persistirse (campos vacíos, formatos inválidos, montos
 * negativos, etc.).
 *
 * Es una excepción NO verificada (RuntimeException) a propósito: así se
 * puede lanzar desde {@code validar(T obj)} dentro de ArchivoDAO sin
 * necesidad de ampliar la cláusula throws de IRepositorio, y sigue siendo
 * perfectamente capturable de forma explícita en la capa de presentación.
 */
public class ValidacionException extends RuntimeException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
