package Reportes;

import Excepciones.ReporteException;
import java.io.File;

/**
 * Estandariza el comportamiento de cualquier generador de reportes del
 * sistema, sin importar qué tipo de documento produzca ni con qué datos
 * trabaje. Implementada por {@link ReporteEmpleadoPDF} (colilla de pago) y
 * {@link ReportePatronoPDF} (resumen de planilla).
 *
 * @param <T> tipo de datos de entrada que necesita este reporte
 */
public interface IGeneradorReporte<T> {

    /**
     * Genera el reporte a partir de los datos recibidos y lo escribe en
     * la ruta indicada.
     *
     * @return el archivo generado
     * @throws ReporteException si ocurre un error al generar el PDF
     */
    File generar(T datos, String rutaDestino) throws ReporteException;
}
