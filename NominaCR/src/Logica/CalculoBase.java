package Logica;

import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.CalculoNominaException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta base de la capa de lógica de negocio. Define el
 * algoritmo general para calcular una nómina (validar datos, calcular
 * salario bruto, deducciones, impuesto de renta, aportes patronales y
 * salario neto) y deja que cada subclase concreta implemente las reglas
 * específicas de cada paso (por ejemplo, las reglas de Costa Rica en
 * {@link CalculoNominaCR}).
 *
 * Toda la regla de cálculo vive aquí y en sus subclases; la capa de
 * presentación (Swing) solo debe invocar {@link #calcular(Empleado, String)}
 * y mostrar el resultado, nunca calcular nada por su cuenta.
 */
public abstract class CalculoBase {

    protected abstract double calcularSalarioBruto(Empleado empleado);

    protected abstract List<DetalleNomina> calcularDeduccionesTrabajador(double salarioBruto);

    protected abstract double calcularImpuestoRenta(double salarioBruto);

    protected abstract List<DetalleNomina> calcularAportesPatronales(double salarioBruto);

    /**
     * Calcula la nómina completa de un empleado para un período dado.
     * Este es el método "plantilla": siempre sigue el mismo orden de
     * pasos, pero cada paso lo resuelve la subclase concreta.
     *
     * @throws CalculoNominaException si los datos de entrada no son válidos
     */
    public final ResultadoCalculo calcular(Empleado empleado, String periodo) {
        validarEntrada(empleado, periodo);

        double salarioBruto = redondear(calcularSalarioBruto(empleado));
        List<DetalleNomina> deducciones = calcularDeduccionesTrabajador(salarioBruto);
        double impuestoRenta = redondear(calcularImpuestoRenta(salarioBruto));
        List<DetalleNomina> aportes = calcularAportesPatronales(salarioBruto);

        double totalDeducciones = sumar(deducciones);
        double totalAportes = sumar(aportes);
        double salarioNeto = redondear(salarioBruto - totalDeducciones - impuestoRenta);

        Nomina nomina = new Nomina();
        nomina.setIdEmpleado(empleado.getId());
        nomina.setPeriodo(periodo);
        nomina.setSalarioBruto(salarioBruto);
        nomina.setTotalDeduccionesTrabajador(totalDeducciones);
        nomina.setImpuestoRenta(impuestoRenta);
        nomina.setSalarioNeto(salarioNeto);
        nomina.setTotalAportesPatronales(totalAportes);
        nomina.setFechaGeneracion(LocalDate.now().toString());

        List<DetalleNomina> detalleCompleto = new ArrayList<>();
        detalleCompleto.addAll(deducciones);
        detalleCompleto.addAll(aportes);

        return new ResultadoCalculo(nomina, detalleCompleto);
    }

    /**
     * Validaciones comunes antes de calcular. Las subclases pueden
     * sobrescribirlo si necesitan reglas adicionales, siempre llamando a
     * super.validarEntrada(...) primero.
     */
    protected void validarEntrada(Empleado empleado, String periodo) {
        if (empleado == null || empleado.getId() <= 0) {
            throw new CalculoNominaException("Debe indicarse un empleado válido para calcular la nómina.");
        }
        if (periodo == null || periodo.trim().isEmpty()) {
            throw new CalculoNominaException("Debe indicarse el período de la nómina.");
        }
        if (empleado.getSalarioBase() <= 0) {
            throw new CalculoNominaException(
                    "El empleado " + empleado.getNombreCompleto() + " no tiene un salario base válido.");
        }
        if (empleado.getHorasExtra() < 0) {
            throw new CalculoNominaException("Las horas extra no pueden ser negativas.");
        }
    }

    protected double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private double sumar(List<DetalleNomina> lineas) {
        double total = 0.0;
        for (DetalleNomina d : lineas) {
            total += d.getMonto();
        }
        return redondear(total);
    }
}
