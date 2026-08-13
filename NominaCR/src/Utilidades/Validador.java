package Utilidades;

import java.util.regex.Pattern;

/**
 * Métodos estáticos de validación reutilizados por la capa de acceso a
 * datos (antes de escribir en archivo) y, más adelante, por los formularios
 * de la capa de presentación (Etapa 4). Centralizarlos aquí evita repetir
 * la misma expresión regular o el mismo chequeo en varios lugares.
 */
public final class Validador {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private Validador() {
        // clase de utilidades: no se instancia
    }

    public static boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean esCorreoValido(String correo) {
        return !esVacio(correo) && PATRON_CORREO.matcher(correo.trim()).matches();
    }

    public static boolean esPositivo(double valor) {
        return valor >= 0;
    }
}
