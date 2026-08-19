package Excepciones;

/**
 * Se lanza cuando los datos de entrada para calcular una nómina no son
 * válidos (empleado inexistente, salario base inválido, período vacío,
 * etc.). Es una excepción de negocio, no de E/S, por lo que —igual que
 * {@link ValidacionException}— es no verificada (RuntimeException): así la
 * capa de presentación la puede capturar explícitamente sin que esto
 * obligue a ensuciar con "throws" cada método de la capa de lógica que no
 * tiene nada que ver con archivos.
 */
public class CalculoNominaException extends RuntimeException {

    public CalculoNominaException(String mensaje) {
        super(mensaje);
    }
}
