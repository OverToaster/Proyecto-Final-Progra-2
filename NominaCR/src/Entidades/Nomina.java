package Entidades;

/**
 * Representa el resumen de una corrida de nómina: un empleado, un período,
 * y los montos totales ya calculados por la capa de lógica (Etapa 3). El
 * desglose línea por línea de cada deducción/aporte vive en
 * {@link DetalleNomina}.
 */
public class Nomina {

    private int id;
    private int idEmpleado;
    private String periodo;
    private double salarioBruto;
    private double totalDeduccionesTrabajador;
    private double impuestoRenta;
    private double salarioNeto;
    private double totalAportesPatronales;
    private String fechaGeneracion;

    public Nomina() {
        limpiar();
    }

    public final void limpiar() {
        this.id = 0;
        this.idEmpleado = 0;
        this.periodo = "";
        this.salarioBruto = 0.0;
        this.totalDeduccionesTrabajador = 0.0;
        this.impuestoRenta = 0.0;
        this.salarioNeto = 0.0;
        this.totalAportesPatronales = 0.0;
        this.fechaGeneracion = "";
    }

    @Override
    public String toString() {
        return "Nomina id=" + this.id + " Empleado=" + this.idEmpleado
                + " Periodo=" + this.periodo + " Neto=" + this.salarioNeto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public double getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public double getTotalDeduccionesTrabajador() {
        return totalDeduccionesTrabajador;
    }

    public void setTotalDeduccionesTrabajador(double totalDeduccionesTrabajador) {
        this.totalDeduccionesTrabajador = totalDeduccionesTrabajador;
    }

    public double getImpuestoRenta() {
        return impuestoRenta;
    }

    public void setImpuestoRenta(double impuestoRenta) {
        this.impuestoRenta = impuestoRenta;
    }

    public double getSalarioNeto() {
        return salarioNeto;
    }

    public void setSalarioNeto(double salarioNeto) {
        this.salarioNeto = salarioNeto;
    }

    public double getTotalAportesPatronales() {
        return totalAportesPatronales;
    }

    public void setTotalAportesPatronales(double totalAportesPatronales) {
        this.totalAportesPatronales = totalAportesPatronales;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
