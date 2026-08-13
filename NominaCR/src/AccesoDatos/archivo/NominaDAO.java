package AccesoDatos.archivo;

import AccesoDatos.base.ArchivoDAO;
import Entidades.Nomina;
import Excepciones.ArchivoInvalidoException;
import Excepciones.ValidacionException;
import Utilidades.NombresArchivos;
import Utilidades.Validador;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Nomina. Formato de línea en nominas.txt:
 * id,idEmpleado,periodo,salarioBruto,totalDeduccionesTrabajador,
 * impuestoRenta,salarioNeto,totalAportesPatronales,fechaGeneracion
 */
public class NominaDAO extends ArchivoDAO<Nomina> {

    public NominaDAO() throws IOException {
        super(NombresArchivos.NOMINAS.getNombreArchivo());
    }

    @Override
    protected int getId(Nomina obj) {
        return obj.getId();
    }

    @Override
    protected void setId(Nomina obj, int id) {
        obj.setId(id);
    }

    @Override
    protected String toLinea(Nomina obj) {
        return obj.getId() + "," + obj.getIdEmpleado() + "," + obj.getPeriodo() + ","
                + obj.getSalarioBruto() + "," + obj.getTotalDeduccionesTrabajador() + ","
                + obj.getImpuestoRenta() + "," + obj.getSalarioNeto() + ","
                + obj.getTotalAportesPatronales() + "," + obj.getFechaGeneracion();
    }

    @Override
    protected void llenarDesdeLinea(Nomina obj, String linea) throws ArchivoInvalidoException {
        try {
            String[] datos = linea.split(",", -1);
            obj.setId(Integer.parseInt(datos[0].trim()));
            obj.setIdEmpleado(Integer.parseInt(datos[1].trim()));
            obj.setPeriodo(datos[2].trim());
            obj.setSalarioBruto(Double.parseDouble(datos[3].trim()));
            obj.setTotalDeduccionesTrabajador(Double.parseDouble(datos[4].trim()));
            obj.setImpuestoRenta(Double.parseDouble(datos[5].trim()));
            obj.setSalarioNeto(Double.parseDouble(datos[6].trim()));
            obj.setTotalAportesPatronales(Double.parseDouble(datos[7].trim()));
            obj.setFechaGeneracion(datos[8].trim());
        } catch (RuntimeException e) {
            throw new ArchivoInvalidoException(
                    "Línea de nómina con formato inválido: \"" + linea + "\"", e);
        }
    }

    @Override
    protected void limpiar(Nomina obj) {
        obj.limpiar();
    }

    @Override
    protected Nomina crearVacio() {
        return new Nomina();
    }

    @Override
    protected void validar(Nomina obj) {
        if (obj.getIdEmpleado() <= 0) {
            throw new ValidacionException("La nómina debe estar asociada a un empleado válido.");
        }
        if (Validador.esVacio(obj.getPeriodo())) {
            throw new ValidacionException("El período de la nómina es obligatorio.");
        }
    }

    /**
     * Devuelve todas las nóminas generadas para un empleado específico.
     */
    public List<Nomina> listarPorEmpleado(int idEmpleado) throws IOException {
        List<Nomina> todas = new ArrayList<>();
        listar(todas);
        List<Nomina> filtradas = new ArrayList<>();
        for (Nomina n : todas) {
            if (n.getIdEmpleado() == idEmpleado) {
                filtradas.add(n);
            }
        }
        return filtradas;
    }

    /**
     * Devuelve todas las nóminas generadas para un período específico
     * (por ejemplo "2026-08"). Se usa para armar el resumen de planilla
     * del patrono en la Etapa 5.
     */
    public List<Nomina> listarPorPeriodo(String periodo) throws IOException {
        List<Nomina> todas = new ArrayList<>();
        listar(todas);
        List<Nomina> filtradas = new ArrayList<>();
        for (Nomina n : todas) {
            if (n.getPeriodo().equals(periodo)) {
                filtradas.add(n);
            }
        }
        return filtradas;
    }
}
