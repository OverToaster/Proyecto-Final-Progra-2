package Presentacion.controladores;

import Entidades.Usuario;
import Excepciones.AutenticacionFallidaException;
import Logica.ServicioAutenticacion;
import java.io.IOException;

/**
 * Controlador de la pantalla de login. Aquí vive el conteo de intentos
 * fallidos y la decisión de bloquear el acceso; LoginFrame solo pregunta
 * "¿puedo intentar?" y "¿estoy bloqueado?", nunca decide nada por su cuenta.
 */
public class LoginControlador {

    private static final int MAX_INTENTOS = 3;

    private final ServicioAutenticacion servicioAutenticacion;
    private int intentosFallidos;

    public LoginControlador() throws IOException {
        this.servicioAutenticacion = new ServicioAutenticacion();
        this.intentosFallidos = 0;
    }

    public Usuario intentarLogin(String username, String password) throws AutenticacionFallidaException, IOException {
        try {
            Usuario usuario = servicioAutenticacion.autenticar(username, password);
            intentosFallidos = 0;
            return usuario;
        } catch (AutenticacionFallidaException e) {
            intentosFallidos++;
            throw e;
        }
    }

    public int getIntentosRestantes() {
        return Math.max(0, MAX_INTENTOS - intentosFallidos);
    }

    public boolean bloqueado() {
        return intentosFallidos >= MAX_INTENTOS;
    }

    public static int getMaxIntentos() {
        return MAX_INTENTOS;
    }

    /**
     * Indica si ya existe al menos un usuario registrado en usuarios.txt.
     */
    public boolean hayUsuariosRegistrados() throws IOException {
        return servicioAutenticacion.hayUsuarios();
    }

    /**
     * Registra un nuevo usuario. Delega toda la validación y la regla de
     * "usuario ya existe" a la capa de lógica.
     */
    public Usuario registrarUsuario(String username, String password, String rol) throws IOException {
        return servicioAutenticacion.registrar(username, password, rol);
    }
}
