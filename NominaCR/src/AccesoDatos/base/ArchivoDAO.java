package AccesoDatos.base;

import AccesoDatos.contratos.IRepositorio;
import Excepciones.ArchivoInvalidoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO genérico basado en archivo de texto plano (una línea por registro,
 * campos separados por coma). Cada entidad concreta solo necesita
 * implementar los métodos abstractos que le indican a esta clase cómo
 * leerse/escribirse a sí misma; toda la mecánica de E/S (abrir, leer línea
 * por línea, reescribir el archivo completo, etc.) vive aquí una sola vez.
 *
 * @param <T> tipo de entidad que administra este DAO
 */
public abstract class ArchivoDAO<T> implements IRepositorio<T> {

    protected final String nombreArchivo;
    protected final IdControl idControl;

    public ArchivoDAO(String nombreArchivo) throws IOException {
        this.nombreArchivo = nombreArchivo;
        this.idControl = IdControl.getInstance();
        asegurarArchivo();
    }

    private void asegurarArchivo() throws IOException {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            archivo.createNewFile();
        }
    }

    // Cada DAO concreto (uno por entidad) implementa estos métodos.
    protected abstract int getId(T obj);
    protected abstract void setId(T obj, int id);
    protected abstract String toLinea(T obj);
    protected abstract void llenarDesdeLinea(T obj, String linea) throws ArchivoInvalidoException;
    protected abstract void limpiar(T obj);
    protected abstract T crearVacio();

    /**
     * Valida los datos de la entidad antes de persistirlos. Implementación
     * por defecto: no valida nada (para no romper compatibilidad); cada DAO
     * concreto la sobrescribe con sus propias reglas y lanza
     * {@link Excepciones.ValidacionException} si algo no es válido.
     */
    protected void validar(T obj) {
        // sin reglas por defecto
    }

    @Override
    public void crear(T obj) throws IOException {
        validar(obj);

        if (getId(obj) == 0) {
            int id = idControl.getSiguienteId(nombreArchivo);
            setId(obj, id);
        }

        try (BufferedWriter bW = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            bW.append(toLinea(obj));
            bW.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "No se pudo escribir en " + nombreArchivo, e);
        }
    }

    @Override
    public void cargarPorId(T obj) throws IOException {
        int idObjeto = getId(obj);
        if (idObjeto <= 0) {
            limpiar(obj);
            setId(obj, 0);
            return;
        }

        try (BufferedReader bR = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = bR.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                int idLinea = extraerId(linea);
                if (idLinea == idObjeto) {
                    llenarDesdeLinea(obj, linea);
                    return;
                }
            }
        } catch (ArchivoInvalidoException e) {
            throw e;
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "No se pudo leer " + nombreArchivo, e);
        }

        limpiar(obj);
        setId(obj, 0);
    }

    @Override
    public void actualizar(T obj) throws IOException {
        validar(obj);

        List<String> lineas = leerTodasLasLineas();
        boolean encontrado = false;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea.isEmpty()) {
                continue;
            }
            if (extraerId(linea) == getId(obj)) {
                lineas.set(i, toLinea(obj));
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                    "No existe un registro con id=" + getId(obj) + " en " + nombreArchivo);
        }

        escribirTodasLasLineas(lineas);
    }

    @Override
    public void eliminarPorId(int id) throws IOException {
        List<String> lineas = leerTodasLasLineas();
        List<String> restantes = new ArrayList<>();
        boolean encontrado = false;

        for (String linea : lineas) {
            if (linea.isEmpty()) {
                continue;
            }
            if (extraerId(linea) == id) {
                encontrado = true;
                continue;
            }
            restantes.add(linea);
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                    "No existe un registro con id=" + id + " en " + nombreArchivo);
        }

        escribirTodasLasLineas(restantes);
    }

    @Override
    public void listar(List<T> destino) throws IOException {
        for (String linea : leerTodasLasLineas()) {
            if (linea.isEmpty()) {
                continue;
            }
            T obj = crearVacio();
            llenarDesdeLinea(obj, linea);
            destino.add(obj);
        }
    }

    // ---------- helpers internos ----------

    private int extraerId(String linea) throws ArchivoInvalidoException {
        try {
            String[] datos = linea.split(",");
            return Integer.parseInt(datos[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new ArchivoInvalidoException(
                    "Línea con formato inválido en " + nombreArchivo + ": \"" + linea + "\"", e);
        }
    }

    private List<String> leerTodasLasLineas() throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader bR = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = bR.readLine()) != null) {
                lineas.add(linea.trim());
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No se pudo leer " + nombreArchivo, e);
        }
        return lineas;
    }

    private void escribirTodasLasLineas(List<String> lineas) throws IOException {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(nombreArchivo, false))) {
            for (String linea : lineas) {
                if (linea.isEmpty()) {
                    continue;
                }
                bW.append(linea);
                bW.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No se pudo escribir en " + nombreArchivo, e);
        }
    }
}
