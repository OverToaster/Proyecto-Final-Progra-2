package Excepciones;

/**
 * Se lanza cuando un intento de inicio de sesión falla: usuario
 * inexistente, contraseña incorrecta, o campos vacíos. Es una excepción
 * verificada (extiende Exception, no RuntimeException) a propósito: el
 * login es un punto de control crítico y se quiere obligar al controlador
 * a manejarla explícitamente, sin que sea posible olvidarla por accidente.
 */
public class AutenticacionFallidaException extends Exception {

    public AutenticacionFallidaException(String mensaje) {
        super(mensaje);
    }
}
