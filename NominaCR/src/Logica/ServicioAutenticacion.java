package Logica;

import AccesoDatos.archivo.UsuarioDAO;
import Entidades.Usuario;
import Excepciones.AutenticacionFallidaException;
import java.io.IOException;

/**
 * Contiene la regla de negocio de autenticación: verificar credenciales
 * contra los usuarios almacenados. Vive en la capa de lógica, no en la
 * pantalla de login, para cumplir con "la ventana nunca calcula ni decide,
 * solo muestra".
 */
public class ServicioAutenticacion {

    private final UsuarioDAO usuarioDAO;

    public ServicioAutenticacion() throws IOException {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Verifica usuario y contraseña. Devuelve el Usuario autenticado si
     * las credenciales son correctas, o lanza AutenticacionFallidaException
     * en caso contrario.
     */
    public Usuario autenticar(String username, String password) throws AutenticacionFallidaException, IOException {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            throw new AutenticacionFallidaException("Debe indicar usuario y contraseña.");
        }

        Usuario usuario = usuarioDAO.buscarPorUsername(username.trim());
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new AutenticacionFallidaException("Usuario o contraseña incorrectos.");
        }
        return usuario;
    }
}
