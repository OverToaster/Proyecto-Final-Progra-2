package AccesoDatos.archivo;

import AccesoDatos.base.ArchivoDAO;
import Entidades.Usuario;
import Excepciones.ValidacionException;
import Utilidades.NombresArchivos;
import Utilidades.Validador;
import java.io.IOException;

/**
 * DAO de Usuario. Formato de línea en usuarios.txt:
 * id,username,password,rol
 */
public class UsuarioDAO extends ArchivoDAO<Usuario> {

    public UsuarioDAO() throws IOException {
        super(NombresArchivos.USUARIOS.getNombreArchivo());
    }

    @Override
    protected int getId(Usuario obj) {
        return obj.getId();
    }

    @Override
    protected void setId(Usuario obj, int id) {
        obj.setId(id);
    }

    @Override
    protected String toLinea(Usuario obj) {
        return obj.getId() + "," + obj.getUsername() + "," + obj.getPassword() + "," + obj.getRol();
    }

    @Override
    protected void llenarDesdeLinea(Usuario obj, String linea) {
        String[] datos = linea.split(",", -1);
        obj.setId(Integer.parseInt(datos[0].trim()));
        obj.setUsername(datos[1].trim());
        obj.setPassword(datos[2].trim());
        obj.setRol(datos[3].trim());
    }

    @Override
    protected void limpiar(Usuario obj) {
        obj.limpiar();
    }

    @Override
    protected Usuario crearVacio() {
        return new Usuario();
    }

    @Override
    protected void validar(Usuario obj) {
        if (Validador.esVacio(obj.getUsername())) {
            throw new ValidacionException("El nombre de usuario es obligatorio.");
        }
        if (Validador.esVacio(obj.getPassword())) {
            throw new ValidacionException("La contraseña es obligatoria.");
        }
        if (Validador.esVacio(obj.getRol())) {
            throw new ValidacionException("El rol es obligatorio.");
        }
    }

    /**
     * Busca un usuario por su nombre de usuario. Devuelve null si no existe.
     * Se usa desde la capa de lógica para el login (Etapa 4).
     */
    public Usuario buscarPorUsername(String username) throws IOException {
        java.util.List<Usuario> todos = new java.util.ArrayList<>();
        listar(todos);
        for (Usuario u : todos) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
