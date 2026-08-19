package Logica;

import AccesoDatos.archivo.UsuarioDAO;
import Entidades.Usuario;
import Excepciones.AutenticacionFallidaException;
import Excepciones.ValidacionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Indica si ya existe al menos un usuario registrado en usuarios.txt.
     * Se usa en el login para ofrecer el registro del primer usuario
     * cuando el archivo está vacío.
     */
    public boolean hayUsuarios() throws IOException {
        List<Usuario> todos = new ArrayList<>();
        usuarioDAO.listar(todos);
        return !todos.isEmpty();
    }

    /**
     * Registra un nuevo usuario. Valida que los campos no estén vacíos y
     * que el nombre de usuario no esté ya en uso antes de delegar la
     * persistencia al DAO.
     */
    public Usuario registrar(String username, String password, String rol) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidacionException("El nombre de usuario es obligatorio.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidacionException("La contraseña es obligatoria.");
        }
        if (rol == null || rol.trim().isEmpty()) {
            throw new ValidacionException("El rol es obligatorio.");
        }

        Usuario existente = usuarioDAO.buscarPorUsername(username.trim());
        if (existente != null) {
            throw new ValidacionException("Ya existe un usuario con ese nombre de usuario.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username.trim());
        nuevo.setPassword(password);
        nuevo.setRol(rol.trim());
        usuarioDAO.crear(nuevo);
        return nuevo;
    }
}
