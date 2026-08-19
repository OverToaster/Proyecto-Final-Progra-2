package Entidades;

/**
 * Representa a un empleado de la empresa sobre el cual se calculará la
 * nómina.
 */
public class Empleado {

    private int id;
    private String cedula;
    private String nombreCompleto;
    private String puesto;
    private double salarioBase;
    private double horasExtra;
    private String correo;
    private boolean activo;

    public Empleado() {
        limpiar();
    }

    public final void limpiar() {
        this.id = 0;
        this.cedula = "";
        this.nombreCompleto = "";
        this.puesto = "";
        this.salarioBase = 0.0;
        this.horasExtra = 0.0;
        this.correo = "";
        this.activo = true;
    }

    @Override
    public String toString() {
        return "Empleado id=" + this.id + " Cedula=" + this.cedula
                + " Nombre=" + this.nombreCompleto + " Puesto=" + this.puesto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public double getHorasExtra() {
        return horasExtra;
    }

    public void setHorasExtra(double horasExtra) {
        this.horasExtra = horasExtra;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
