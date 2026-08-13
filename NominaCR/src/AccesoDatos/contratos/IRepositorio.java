package AccesoDatos.contratos;

import java.io.IOException;
import java.util.List;

/**
 * Contrato genérico de persistencia que deben cumplir todos los DAO del
 * sistema, sin importar la entidad que manejen.
 */
public interface IRepositorio<T> {

    void crear(T obj) throws IOException;
    void cargarPorId(T obj) throws IOException;
    void actualizar(T obj) throws IOException;
    void eliminarPorId(int id) throws IOException;
    void listar(List<T> destino) throws IOException;
}
