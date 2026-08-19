package Logica;

/**
 * Tabla de tramos del Impuesto sobre la Renta al salario, vigente para el
 * período fiscal 2026 en Costa Rica (Decreto Ejecutivo N° 45333-H,
 * publicado en La Gaceta N° 229 del 05/12/2025, con vigencia desde el
 * 01/01/2026).
 *
 * IMPORTANTE: estos montos se actualizan cada año según el Ministerio de
 * Hacienda. Antes de la entrega final del proyecto, verificar que sigan
 * vigentes contra la publicación oficial en hacienda.go.cr y citarla en la
 * documentación, tal como exige el enunciado.
 *
 * Tramos mensuales para asalariados (2026):
 * Hasta          ¢918.000    -> exento (0%)
 * Exceso hasta   ¢1.347.000  -> 10%
 * Exceso hasta   ¢2.364.000  -> 15%
 * Exceso hasta   ¢4.727.000  -> 20%
 * Exceso sobre   ¢4.727.000  -> 25%
 *
 * El cálculo es progresivo por tramos: cada porcentaje se aplica
 * únicamente sobre la porción del salario que cae dentro de ese tramo, no
 * sobre el salario completo.
 */
public final class TablaRentaCR {

    private static final double[] LIMITES = {918_000, 1_347_000, 2_364_000, 4_727_000};
    private static final double[] TASAS = {0.10, 0.15, 0.20, 0.25};

    private TablaRentaCR() {
        // clase de utilidad: no se instancia
    }

    /**
     * Calcula el impuesto sobre la renta correspondiente a un salario
     * bruto mensual, aplicando la tabla de tramos progresivos.
     */
    public static double calcularImpuesto(double salarioBrutoMensual) {
        if (salarioBrutoMensual <= LIMITES[0]) {
            return 0.0;
        }

        double impuesto = 0.0;
        double limiteInferior = LIMITES[0];

        for (int i = 0; i < LIMITES.length; i++) {
            double limiteSuperior = (i + 1 < LIMITES.length) ? LIMITES[i + 1] : Double.POSITIVE_INFINITY;

            if (salarioBrutoMensual > limiteInferior) {
                double baseGravableDelTramo = Math.min(salarioBrutoMensual, limiteSuperior) - limiteInferior;
                impuesto += baseGravableDelTramo * TASAS[i];
            }

            limiteInferior = limiteSuperior;
        }

        return impuesto;
    }
}
