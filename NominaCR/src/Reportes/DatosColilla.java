package Reportes;

import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import java.util.List;

/**
 * Agrupa los datos que necesita un generador de reportes para producir la
 * colilla de pago de un empleado: sus datos personales, el resumen de la
 * nómina y el detalle línea por línea.
 */
public class DatosColilla {

    private final Empleado empleado;
    private final Nomina nomina;
    private final List<DetalleNomina> detalle;

    public DatosColilla(Empleado empleado, Nomina nomina, List<DetalleNomina> detalle) {
        this.empleado = empleado;
        this.nomina = nomina;
        this.detalle = detalle;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Nomina getNomina() {
        return nomina;
    }

    public List<DetalleNomina> getDetalle() {
        return detalle;
    }
}
