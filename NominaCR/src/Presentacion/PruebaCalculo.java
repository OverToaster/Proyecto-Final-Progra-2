package Presentacion;

import AccesoDatos.archivo.DetalleNominaDAO;
import AccesoDatos.archivo.EmpleadoDAO;
import AccesoDatos.archivo.NominaDAO;
import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Logica.CalculoNominaCR;
import Logica.ResultadoCalculo;

/**
 * Clase de prueba en consola para la Etapa 3: verifica la lógica de
 * cálculo de nómina contra 3 casos calculados a mano (ver comentarios de
 * cada caso), y además demuestra el flujo completo de persistencia
 * (Nomina + DetalleNomina) usando los DAO de la Etapa 2.
 *
 * Tolerancia usada en las comparaciones: 0.5 colones, para absorber
 * redondeos de centavos entre el cálculo manual y el de punto flotante.
 */
public class PruebaCalculo {

    private static final double TOLERANCIA = 0.5;
    private static int casosOk = 0;
    private static int casosFallidos = 0;

    public static void main(String[] args) {
        try {
            System.out.println("=== Etapa 3: verificación de casos calculados a mano ===\n");

            // ---------------------------------------------------------------
            // Caso 1: salario base 800,000, sin horas extra.
            // Cálculo a mano:
            //   Bruto = 800,000 (no hay horas extra)
            //   CCSS SEM (5.50%) = 44,000.00
            //   CCSS IVM (4.17%) = 33,360.00
            //   Banco Popular (1.00%) = 8,000.00
            //   Total deducciones trabajador = 85,360.00
            //   Renta: 800,000 <= 918,000 (tramo exento) => impuesto = 0
            //   Neto = 800,000 - 85,360 - 0 = 714,640.00
            //   Aportes patronales (26.67%) = 800,000 * 0.2667 = 213,360.00
            // ---------------------------------------------------------------
            probarCaso(
                    "Caso 1: salario 800,000, sin horas extra",
                    crearEmpleadoPrueba("Ana Rodríguez", 800_000, 0),
                    "2026-08",
                    800_000, 85_360, 0, 714_640, 213_360);

            // ---------------------------------------------------------------
            // Caso 2: salario base 1,200,000, 10 horas extra.
            // Cálculo a mano:
            //   Hora ordinaria = 1,200,000 / 240 = 5,000.00
            //   Monto horas extra = 10 * 5,000 * 1.5 = 75,000.00
            //   Bruto = 1,200,000 + 75,000 = 1,275,000.00
            //   CCSS SEM (5.50%) = 70,125.00
            //   CCSS IVM (4.17%) = 53,167.50
            //   Banco Popular (1.00%) = 12,750.00
            //   Total deducciones trabajador = 136,042.50
            //   Renta: tramo 918,000-1,347,000 al 10%:
            //     (1,275,000 - 918,000) * 0.10 = 35,700.00
            //   Neto = 1,275,000 - 136,042.50 - 35,700 = 1,103,257.50
            //   Aportes patronales = 1,275,000 * 0.2667 = 340,042.50
            // ---------------------------------------------------------------
            probarCaso(
                    "Caso 2: salario 1,200,000, 10 horas extra",
                    crearEmpleadoPrueba("Carlos Jiménez", 1_200_000, 10),
                    "2026-08",
                    1_275_000, 136_042.50, 35_700, 1_103_257.50, 340_042.50);

            // ---------------------------------------------------------------
            // Caso 3: salario base 3,000,000, sin horas extra.
            // Cálculo a mano:
            //   Bruto = 3,000,000.00
            //   CCSS SEM (5.50%) = 165,000.00
            //   CCSS IVM (4.17%) = 125,100.00
            //   Banco Popular (1.00%) = 30,000.00
            //   Total deducciones trabajador = 320,100.00
            //   Renta por tramos:
            //     918,000-1,347,000 (10%):   429,000 * 0.10 = 42,900.00
            //     1,347,000-2,364,000 (15%): 1,017,000 * 0.15 = 152,550.00
            //     2,364,000-3,000,000 (20%): 636,000 * 0.20 = 127,200.00
            //     Total renta = 322,650.00
            //   Neto = 3,000,000 - 320,100 - 322,650 = 2,357,250.00
            //   Aportes patronales = 3,000,000 * 0.2667 = 800,100.00
            // ---------------------------------------------------------------
            probarCaso(
                    "Caso 3: salario 3,000,000, sin horas extra",
                    crearEmpleadoPrueba("Laura Vargas", 3_000_000, 0),
                    "2026-08",
                    3_000_000, 320_100, 322_650, 2_357_250, 800_100);

            System.out.println("\n=== Resultado: " + casosOk + " caso(s) OK, "
                    + casosFallidos + " caso(s) fallido(s) ===");

            if (casosFallidos == 0) {
                probarPersistenciaCompleta();
            }

        } catch (Exception e) {
            System.out.println("ERROR durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Empleado crearEmpleadoPrueba(String nombre, double salarioBase, double horasExtra) {
        Empleado e = new Empleado();
        e.setId(1); // id ficticio solo para que pase la validación de "empleado válido"
        e.setCedula("0-0000-0000");
        e.setNombreCompleto(nombre);
        e.setPuesto("Puesto de prueba");
        e.setSalarioBase(salarioBase);
        e.setHorasExtra(horasExtra);
        e.setCorreo("prueba@ejemplo.com");
        return e;
    }

    private static void probarCaso(String nombreCaso, Empleado empleado, String periodo,
            double brutoEsperado, double deduccionesEsperadas, double rentaEsperada,
            double netoEsperado, double aportesEsperados) {

        System.out.println("--- " + nombreCaso + " ---");
        CalculoNominaCR calculadora = new CalculoNominaCR();
        ResultadoCalculo resultado = calculadora.calcular(empleado, periodo);
        Nomina n = resultado.getNomina();

        boolean ok = true;
        ok &= verificar("Salario bruto", brutoEsperado, n.getSalarioBruto());
        ok &= verificar("Deducciones trabajador", deduccionesEsperadas, n.getTotalDeduccionesTrabajador());
        ok &= verificar("Impuesto renta", rentaEsperada, n.getImpuestoRenta());
        ok &= verificar("Salario neto", netoEsperado, n.getSalarioNeto());
        ok &= verificar("Aportes patronales", aportesEsperados, n.getTotalAportesPatronales());

        System.out.println("Líneas de detalle generadas: " + resultado.getDetalle().size());
        for (DetalleNomina d : resultado.getDetalle()) {
            System.out.println("  " + d);
        }

        if (ok) {
            casosOk++;
            System.out.println("Resultado: OK\n");
        } else {
            casosFallidos++;
            System.out.println("Resultado: FALLÓ (revisar diferencias arriba)\n");
        }
    }

    private static boolean verificar(String etiqueta, double esperado, double obtenido) {
        double diferencia = Math.abs(esperado - obtenido);
        boolean ok = diferencia <= TOLERANCIA;
        String marca = ok ? "OK  " : "MAL ";
        System.out.printf("  [%s] %-25s esperado=%.2f  obtenido=%.2f%n",
                marca, etiqueta, esperado, obtenido);
        return ok;
    }

    /**
     * Demuestra el flujo completo: calcular una nómina y persistirla
     * (Nomina primero para obtener su id, luego cada línea de
     * DetalleNomina con ese id ya asignado), reutilizando los DAO de la
     * Etapa 2.
     */
    private static void probarPersistenciaCompleta() throws Exception {
        System.out.println("\n--- Prueba de persistencia completa (Logica + AccesoDatos) ---");

        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        NominaDAO nominaDAO = new NominaDAO();
        DetalleNominaDAO detalleDAO = new DetalleNominaDAO();

        Empleado empleado = new Empleado();
        empleado.setCedula("3-4567-8901");
        empleado.setNombreCompleto("Mariana Solís Vega");
        empleado.setPuesto("Contadora");
        empleado.setSalarioBase(950_000);
        empleado.setHorasExtra(5);
        empleado.setCorreo("mariana.solis@ejemplo.com");
        empleadoDAO.crear(empleado);
        System.out.println("Empleado creado: " + empleado);

        CalculoNominaCR calculadora = new CalculoNominaCR();
        ResultadoCalculo resultado = calculadora.calcular(empleado, "2026-08");

        Nomina nomina = resultado.getNomina();
        nominaDAO.crear(nomina);
        System.out.println("Nómina calculada y guardada: " + nomina);

        for (DetalleNomina d : resultado.getDetalle()) {
            d.setIdNomina(nomina.getId());
            detalleDAO.crear(d);
        }
        System.out.println("Detalle guardado: " + resultado.getDetalle().size() + " líneas.");

        // limpieza de los datos de prueba
        for (DetalleNomina d : detalleDAO.listarPorNomina(nomina.getId())) {
            detalleDAO.eliminarPorId(d.getId());
        }
        nominaDAO.eliminarPorId(nomina.getId());
        empleadoDAO.eliminarPorId(empleado.getId());
        System.out.println("Datos de prueba limpiados correctamente.");
    }
}
