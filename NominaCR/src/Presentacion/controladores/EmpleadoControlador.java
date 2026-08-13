package Presentacion.controladores;

import AccesoDatos.archivo.EmpleadoDAO;
import Entidades.Empleado;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la pantalla de gestión de empleados. Las reglas de
 * validación de datos (campos vacíos, correo válido, salario positivo)
 * viven en EmpleadoDAO.validar (capa de acceso a datos, Etapa 2); este
 * controlador solo coordina las llamadas, no repite esas reglas.
 */
public class EmpleadoControlador {

    private final EmpleadoDAO empleadoDAO;

    public EmpleadoControlador() throws IOException {
        this.empleadoDAO = new EmpleadoDAO();
    }

    public List<Empleado> listarEmpleados() throws IOException {
        List<Empleado> lista = new ArrayList<>();
        empleadoDAO.listar(lista);
        return lista;
    }

    public void registrar(Empleado empleado) throws IOException {
        empleadoDAO.crear(empleado);
    }

    public void modificar(Empleado empleado) throws IOException {
        empleadoDAO.actualizar(empleado);
    }

    public void eliminar(int id) throws IOException {
        empleadoDAO.eliminarPorId(id);
    }
}
