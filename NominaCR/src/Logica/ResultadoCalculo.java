package Logica;

import Entidades.DetalleNomina;
import Entidades.Nomina;
import java.util.List;

/**
 * Agrupa el resultado de calcular una nómina: el resumen (Nomina) y sus
 * líneas de detalle (deducciones + aportes patronales). No es una entidad
 * persistente por sí misma: la capa de presentación/controlador es quien
 * decide cuándo y cómo guardar estos datos usando los DAO correspondientes
 * (primero la Nomina, para obtener su id autogenerado, y luego cada
 * DetalleNomina con ese id ya asignado).
 */
public class ResultadoCalculo {

    private final Nomina nomina;
    private final List<DetalleNomina> detalle;

    public ResultadoCalculo(Nomina nomina, List<DetalleNomina> detalle) {
        this.nomina = nomina;
        this.detalle = detalle;
    }

    public Nomina getNomina() {
        return nomina;
    }

    public List<DetalleNomina> getDetalle() {
        return detalle;
    }
}
