package Logica;

import Entidades.DetalleNomina;
import Entidades.DetalleNomina.TipoRubro;
import Entidades.Empleado;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta de {@link CalculoBase} con las reglas de la
 * normativa laboral costarricense: horas extra con recargo del 50%,
 * cargas sociales de la CCSS y el Banco Popular, impuesto sobre la renta
 * por tramos, y aportes patronales.
 *
 * Porcentajes de cargas sociales tomados de la tabla de referencia del
 * enunciado del proyecto (verificar contra la normativa vigente de la
 * CCSS antes de la entrega final, tal como exige el profesor).
 */
public class CalculoNominaCR extends CalculoBase {

    // Horas mensuales de referencia para obtener el valor de la hora
    // ordinaria a partir del salario mensual: 8 horas/día x 30 días.
    private static final double HORAS_MES = 240.0;
    private static final double RECARGO_HORA_EXTRA = 1.5;

    // ---- Porcentajes sobre salario bruto: deducción al trabajador ----
    private static final double CCSS_SEM_TRABAJADOR = 0.0550;
    private static final double CCSS_IVM_TRABAJADOR = 0.0417;
    private static final double BANCO_POPULAR_TRABAJADOR = 0.0100;

    // ---- Porcentajes sobre salario bruto: aporte patronal ----
    private static final double CCSS_SEM_PATRONAL = 0.0925;
    private static final double CCSS_IVM_PATRONAL = 0.0542;
    private static final double BANCO_POPULAR_PATRONAL = 0.0050;
    private static final double FODESAF_PATRONAL = 0.0500;
    private static final double IMAS_PATRONAL = 0.0050;
    private static final double INA_PATRONAL = 0.0150;
    private static final double FCL_PATRONAL = 0.0150;
    private static final double ROP_PATRONAL = 0.0200;
    private static final double INS_PATRONAL = 0.0100;

    @Override
    protected double calcularSalarioBruto(Empleado empleado) {
        double horaOrdinaria = empleado.getSalarioBase() / HORAS_MES;
        double montoHorasExtra = empleado.getHorasExtra() * horaOrdinaria * RECARGO_HORA_EXTRA;
        return empleado.getSalarioBase() + montoHorasExtra;
    }

    @Override
    protected List<DetalleNomina> calcularDeduccionesTrabajador(double salarioBruto) {
        List<DetalleNomina> lineas = new ArrayList<>();
        lineas.add(linea("CCSS - Seguro de Enfermedad y Maternidad (SEM)",
                salarioBruto * CCSS_SEM_TRABAJADOR, TipoRubro.DEDUCCION_TRABAJADOR));
        lineas.add(linea("CCSS - Invalidez, Vejez y Muerte (IVM)",
                salarioBruto * CCSS_IVM_TRABAJADOR, TipoRubro.DEDUCCION_TRABAJADOR));
        lineas.add(linea("Banco Popular (aporte trabajador)",
                salarioBruto * BANCO_POPULAR_TRABAJADOR, TipoRubro.DEDUCCION_TRABAJADOR));
        return lineas;
    }

    @Override
    protected double calcularImpuestoRenta(double salarioBruto) {
        return TablaRentaCR.calcularImpuesto(salarioBruto);
    }

    @Override
    protected List<DetalleNomina> calcularAportesPatronales(double salarioBruto) {
        List<DetalleNomina> lineas = new ArrayList<>();
        lineas.add(linea("CCSS - Seguro de Enfermedad y Maternidad (SEM)",
                salarioBruto * CCSS_SEM_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("CCSS - Invalidez, Vejez y Muerte (IVM)",
                salarioBruto * CCSS_IVM_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("Banco Popular (aporte patronal)",
                salarioBruto * BANCO_POPULAR_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("Asignaciones Familiares (FODESAF)",
                salarioBruto * FODESAF_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("IMAS",
                salarioBruto * IMAS_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("INA",
                salarioBruto * INA_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("Fondo de Capitalización Laboral (FCL)",
                salarioBruto * FCL_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("Régimen Obligatorio de Pensiones (ROP)",
                salarioBruto * ROP_PATRONAL, TipoRubro.APORTE_PATRONAL));
        lineas.add(linea("Instituto Nacional de Seguros (INS)",
                salarioBruto * INS_PATRONAL, TipoRubro.APORTE_PATRONAL));
        return lineas;
    }

    private DetalleNomina linea(String concepto, double monto, TipoRubro tipo) {
        DetalleNomina d = new DetalleNomina();
        d.setConcepto(concepto);
        d.setMonto(redondear(monto));
        d.setTipo(tipo);
        return d;
    }
}
