package AccesoDatos.archivo;

import AccesoDatos.base.ArchivoDAO;
import Entidades.Empleado;
import Excepciones.ArchivoInvalidoException;
import Excepciones.ValidacionException;
import Utilidades.NombresArchivos;
import Utilidades.Validador;
import java.io.IOException;

/**
 * DAO de Empleado. Formato de línea en empleados.txt:
 * id,cedula,nombreCompleto,puesto,salarioBase,horasExtra,correo,activo
 */
public class EmpleadoDAO extends ArchivoDAO<Empleado> {

    public EmpleadoDAO() throws IOException {
        super(NombresArchivos.EMPLEADOS.getNombreArchivo());
    }

    @Override
    protected int getId(Empleado obj) {
        return obj.getId();
    }

    @Override
    protected void setId(Empleado obj, int id) {
        obj.setId(id);
    }

    @Override
    protected String toLinea(Empleado obj) {
        return obj.getId() + "," + obj.getCedula() + "," + obj.getNombreCompleto() + ","
                + obj.getPuesto() + "," + obj.getSalarioBase() + "," + obj.getHorasExtra() + ","
                + obj.getCorreo() + "," + obj.isActivo();
    }

    @Override
    protected void llenarDesdeLinea(Empleado obj, String linea) throws ArchivoInvalidoException {
        try {
            String[] datos = linea.split(",", -1);
            obj.setId(Integer.parseInt(datos[0].trim()));
            obj.setCedula(datos[1].trim());
            obj.setNombreCompleto(datos[2].trim());
            obj.setPuesto(datos[3].trim());
            obj.setSalarioBase(Double.parseDouble(datos[4].trim()));
            obj.setHorasExtra(Double.parseDouble(datos[5].trim()));
            obj.setCorreo(datos[6].trim());
            obj.setActivo(Boolean.parseBoolean(datos[7].trim()));
        } catch (RuntimeException e) {
            throw new ArchivoInvalidoException(
                    "Línea de empleado con formato inválido: \"" + linea + "\"", e);
        }
    }

    @Override
    protected void limpiar(Empleado obj) {
        obj.limpiar();
    }

    @Override
    protected Empleado crearVacio() {
        return new Empleado();
    }

    @Override
    protected void validar(Empleado obj) {
        if (Validador.esVacio(obj.getCedula())) {
            throw new ValidacionException("La cédula es obligatoria.");
        }
        if (Validador.esVacio(obj.getNombreCompleto())) {
            throw new ValidacionException("El nombre completo es obligatorio.");
        }
        if (!Validador.esPositivo(obj.getSalarioBase()) || obj.getSalarioBase() == 0) {
            throw new ValidacionException("El salario base debe ser mayor a cero.");
        }
        if (!Validador.esPositivo(obj.getHorasExtra())) {
            throw new ValidacionException("Las horas extra no pueden ser negativas.");
        }
        if (!Validador.esCorreoValido(obj.getCorreo())) {
            throw new ValidacionException("El correo del empleado no es válido.");
        }
    }
}
