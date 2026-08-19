package AccesoDatos.archivo;

import AccesoDatos.base.ArchivoDAO;
import Entidades.DetalleNomina;
import Excepciones.ArchivoInvalidoException;
import Excepciones.ValidacionException;
import Utilidades.NombresArchivos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de DetalleNomina. Formato de línea en detalle_nomina.txt:
 * id,idNomina,concepto,monto,tipo
 */
public class DetalleNominaDAO extends ArchivoDAO<DetalleNomina> {

    public DetalleNominaDAO() throws IOException {
        super(NombresArchivos.DETALLE_NOMINA.getNombreArchivo());
    }

    @Override
    protected int getId(DetalleNomina obj) {
        return obj.getId();
    }

    @Override
    protected void setId(DetalleNomina obj, int id) {
        obj.setId(id);
    }

    @Override
    protected String toLinea(DetalleNomina obj) {
        return obj.getId() + "," + obj.getIdNomina() + "," + obj.getConcepto() + ","
                + obj.getMonto() + "," + obj.getTipo();
    }

    @Override
    protected void llenarDesdeLinea(DetalleNomina obj, String linea) throws ArchivoInvalidoException {
        try {
            String[] datos = linea.split(",", -1);
            obj.setId(Integer.parseInt(datos[0].trim()));
            obj.setIdNomina(Integer.parseInt(datos[1].trim()));
            obj.setConcepto(datos[2].trim());
            obj.setMonto(Double.parseDouble(datos[3].trim()));
            obj.setTipo(DetalleNomina.TipoRubro.valueOf(datos[4].trim()));
        } catch (RuntimeException e) {
            throw new ArchivoInvalidoException(
                    "Línea de detalle de nómina con formato inválido: \"" + linea + "\"", e);
        }
    }

    @Override
    protected void limpiar(DetalleNomina obj) {
        obj.limpiar();
    }

    @Override
    protected DetalleNomina crearVacio() {
        return new DetalleNomina();
    }

    @Override
    protected void validar(DetalleNomina obj) {
        if (obj.getIdNomina() <= 0) {
            throw new ValidacionException("El detalle debe estar asociado a una nómina válida.");
        }
    }

    /**
     * Devuelve todas las líneas de detalle que pertenecen a una nómina.
     */
    public List<DetalleNomina> listarPorNomina(int idNomina) throws IOException {
        List<DetalleNomina> todos = new ArrayList<>();
        listar(todos);
        List<DetalleNomina> filtrados = new ArrayList<>();
        for (DetalleNomina d : todos) {
            if (d.getIdNomina() == idNomina) {
                filtrados.add(d);
            }
        }
        return filtrados;
    }
}
