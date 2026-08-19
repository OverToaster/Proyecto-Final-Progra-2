package Entidades;

/**
 * Representa una línea individual del desglose de una nómina: un rubro
 * (por ejemplo "CCSS SEM" o "Impuesto sobre la renta") con su monto y el
 * tipo de rubro. Varias líneas de DetalleNomina componen el desglose
 * completo de una {@link Nomina}.
 */
public class DetalleNomina {

    public enum TipoRubro {
        DEDUCCION_TRABAJADOR,
        APORTE_PATRONAL
    }

    private int id;
    private int idNomina;
    private String concepto;
    private double monto;
    private TipoRubro tipo;

    public DetalleNomina() {
        limpiar();
    }

    public final void limpiar() {
        this.id = 0;
        this.idNomina = 0;
        this.concepto = "";
        this.monto = 0.0;
        this.tipo = TipoRubro.DEDUCCION_TRABAJADOR;
    }

    @Override
    public String toString() {
        return "DetalleNomina id=" + this.id + " Nomina=" + this.idNomina
                + " Concepto=" + this.concepto + " Monto=" + this.monto
                + " Tipo=" + this.tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdNomina() {
        return idNomina;
    }

    public void setIdNomina(int idNomina) {
        this.idNomina = idNomina;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public TipoRubro getTipo() {
        return tipo;
    }

    public void setTipo(TipoRubro tipo) {
        this.tipo = tipo;
    }
}
