package AccesoDatos.base;

import Utilidades.NombresArchivos;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lleva el control del siguiente ID autogenerado disponible para cada
 * archivo de datos, persistido en id_control.txt en formato
 * "nombreArchivo=siguienteId" (una línea por archivo).
 *
 * Es un singleton: solo debe existir una instancia por ejecución, para que
 * dos DAOs distintos nunca entreguen el mismo ID.
 */
public class IdControl {

    private static IdControl instancia;
    private final String archivoControl;
    private final Map<String, Integer> idMap;

    private IdControl() throws IOException {
        this.archivoControl = NombresArchivos.ID_CONTROL.getNombreArchivo();
        this.idMap = new HashMap<>();
        asegurarArchivo();
        cargarIds();
    }

    private void asegurarArchivo() throws IOException {
        File file = new File(this.archivoControl);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static synchronized IdControl getInstance() throws IOException {
        if (instancia == null) {
            instancia = new IdControl();
        }
        return instancia;
    }

    private void cargarIds() throws IOException {
        idMap.clear();
        try (BufferedReader bR = new BufferedReader(new FileReader(archivoControl))) {
            String linea;
            while ((linea = bR.readLine()) != null) {
                linea = linea.trim();
                // Solo se procesan líneas no vacías que sí contienen "=".
                // (En la plantilla original esta condición estaba invertida
                // y por eso nunca se recargaban los IDs guardados.)
                if (linea.isEmpty() || !linea.contains("=")) {
                    continue;
                }
                String[] datos = linea.split("=");
                if (datos.length != 2) {
                    continue;
                }
                idMap.put(datos[0].trim(), Integer.valueOf(datos[1].trim()));
            }
        }
    }

    public synchronized int getSiguienteId(String nombreArchivo) throws IOException {
        cargarIds();
        int siguienteId = idMap.getOrDefault(nombreArchivo, 1);
        idMap.put(nombreArchivo, siguienteId + 1);
        guardarIds();
        return siguienteId;
    }

    private void guardarIds() throws IOException {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(archivoControl))) {
            for (Map.Entry<String, Integer> entrada : idMap.entrySet()) {
                bW.append(entrada.getKey() + "=" + entrada.getValue());
                bW.newLine();
            }
        }
    }
}
