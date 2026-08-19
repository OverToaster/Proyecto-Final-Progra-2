package Presentacion;

import AccesoDatos.archivo.DetalleNominaDAO;
import AccesoDatos.archivo.EmpleadoDAO;
import AccesoDatos.archivo.NominaDAO;
import AccesoDatos.archivo.UsuarioDAO;
import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Entidades.Usuario;
import Excepciones.CorreoException;
import Logica.CalculoNominaCR;
import Logica.ResultadoCalculo;
import Logica.ServicioAutenticacion;
import Presentacion.controladores.NominaControlador;
import Reportes.DatosColilla;
import Reportes.ReporteEmpleadoPDF;
import java.io.File;

/**
 * Etapa 6: prueba de integración del flujo completo, desde la
 * autenticación hasta el intento de envío de correo, reutilizando todas
 * las capas construidas en las etapas anteriores (login, empleados,
 * cálculo, persistencia, reportes, correo).
 *
 * El envío real de correo requiere credenciales válidas en
 * Utilidades.ConfiguracionCorreo. Si no se han configurado (valores de
 * ejemplo por defecto), el intento de envío fallará de forma controlada
 * con CorreoException, y esta prueba lo reporta como comportamiento
 * esperado, no como error del sistema: precisamente demuestra que el
 * error de correo se maneja con un mensaje claro y no con un stack trace.
 */
public class PruebaIntegracion {

    public static void main(String[] args) {
        System.out.println("=== Etapa 6: prueba de integración (login -> ... -> correo) ===\n");

        Empleado empleadoCreado = null;
        Nomina nominaCreada = null;
        File pdfGenerado = null;

        try {
            System.out.println("Paso 1: autenticación");
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsername("integracion_test");
            usuarioPrueba.setPassword("clave123");
            usuarioPrueba.setRol("ADMIN");
            usuarioDAO.crear(usuarioPrueba);

            ServicioAutenticacion servicioAuth = new ServicioAutenticacion();
            Usuario autenticado = servicioAuth.autenticar("integracion_test", "clave123");
            System.out.println("  OK - autenticado como " + autenticado.getUsername());

            System.out.println("Paso 2: registro de empleado");
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            empleadoCreado = new Empleado();
            empleadoCreado.setCedula("4-5678-9012");
            empleadoCreado.setNombreCompleto("Prueba Integración");
            empleadoCreado.setPuesto("QA");
            empleadoCreado.setSalarioBase(900000);
            empleadoCreado.setHorasExtra(3);
            empleadoCreado.setCorreo("integracion@ejemplo.com");
            empleadoDAO.crear(empleadoCreado);
            System.out.println("  OK - empleado creado con id=" + empleadoCreado.getId());

            System.out.println("Paso 3: cálculo de nómina");
            CalculoNominaCR calculadora = new CalculoNominaCR();
            ResultadoCalculo resultado = calculadora.calcular(empleadoCreado, "2026-08");
            System.out.println("  OK - salario neto calculado: " + resultado.getNomina().getSalarioNeto());

            System.out.println("Paso 4: guardar nómina y detalle");
            NominaDAO nominaDAO = new NominaDAO();
            DetalleNominaDAO detalleDAO = new DetalleNominaDAO();
            nominaCreada = resultado.getNomina();
            nominaDAO.crear(nominaCreada);
            for (DetalleNomina d : resultado.getDetalle()) {
                d.setIdNomina(nominaCreada.getId());
                detalleDAO.crear(d);
            }
            System.out.println("  OK - nómina id=" + nominaCreada.getId() + " guardada con "
                    + resultado.getDetalle().size() + " líneas de detalle.");

            System.out.println("Paso 5: generación de la colilla en PDF");
            DatosColilla datosColilla = new DatosColilla(empleadoCreado, nominaCreada, resultado.getDetalle());
            pdfGenerado = new ReporteEmpleadoPDF().generar(datosColilla,
                    System.getProperty("java.io.tmpdir") + File.separator + "colilla_prueba_integracion.pdf");
            System.out.println("  OK - PDF generado en " + pdfGenerado.getAbsolutePath());

            System.out.println("Paso 6: envío de correo");
            try {
                NominaControlador controladorNomina = new NominaControlador();
                controladorNomina.enviarColillaPorCorreo(empleadoCreado, "2026-08", pdfGenerado);
                System.out.println("  OK - correo enviado.");
            } catch (CorreoException e) {
                System.out.println("  AVISO (esperado si no configuró credenciales reales en "
                        + "Utilidades.ConfiguracionCorreo): " + e.getMessage());
            }

            System.out.println("\n=== Flujo de integración completado sin errores no controlados ===");

        } catch (Exception e) {
            System.out.println("ERROR no controlado en la prueba de integración: " + e.getMessage());
            e.printStackTrace();
        } finally {
            limpiar(empleadoCreado, nominaCreada, pdfGenerado);
        }
    }

    private static void limpiar(Empleado empleado, Nomina nomina, File pdf) {
        try {
            System.out.println("\nLimpiando datos de prueba...");
            if (nomina != null) {
                DetalleNominaDAO detalleDAO = new DetalleNominaDAO();
                for (DetalleNomina d : detalleDAO.listarPorNomina(nomina.getId())) {
                    detalleDAO.eliminarPorId(d.getId());
                }
                new NominaDAO().eliminarPorId(nomina.getId());
            }
            if (empleado != null) {
                new EmpleadoDAO().eliminarPorId(empleado.getId());
            }
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario u = usuarioDAO.buscarPorUsername("integracion_test");
            if (u != null) {
                usuarioDAO.eliminarPorId(u.getId());
            }
            if (pdf != null && pdf.exists()) {
                pdf.delete();
            }
            System.out.println("Limpieza completa.");
        } catch (Exception e) {
            System.out.println("Aviso: no se pudo limpiar completamente: " + e.getMessage());
        }
    }
}
