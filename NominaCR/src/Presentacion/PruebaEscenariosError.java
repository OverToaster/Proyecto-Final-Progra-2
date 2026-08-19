package Presentacion;

import AccesoDatos.archivo.DetalleNominaDAO;
import AccesoDatos.archivo.EmpleadoDAO;
import AccesoDatos.archivo.NominaDAO;
import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.ArchivoInvalidoException;
import Excepciones.AutenticacionFallidaException;
import Excepciones.CalculoNominaException;
import Excepciones.ValidacionException;
import Logica.CalculoNominaCR;
import Logica.ServicioAutenticacion;
import Presentacion.controladores.LoginControlador;

/**
 * Etapa 6: pruebas de los escenarios de error exigidos por el enunciado
 * (datos de entrada inválidos, registros inexistentes, login fallido).
 * Cada caso confirma que se lanza exactamente el tipo de excepción
 * esperado. El detalle de cada prueba (escenario, datos de entrada,
 * resultado esperado/obtenido) se documenta en la matriz de pruebas de
 * la Etapa 7.
 */
public class PruebaEscenariosError {

    private static int casosOk = 0;
    private static int casosFallidos = 0;

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Etapa 6: escenarios de error ===\n");

        probarLoginFallido();
        probarEmpleadoDatosInvalidos();
        probarActualizarRegistroInexistente();
        probarEliminarRegistroInexistente();
        probarCalculoDatosInvalidos();

        System.out.println("\n=== Resultado: " + casosOk + " caso(s) OK, "
                + casosFallidos + " caso(s) fallido(s) ===");
    }

    private static void probarLoginFallido() {
        esperarExcepcion("Login con credenciales incorrectas", AutenticacionFallidaException.class, () -> {
            ServicioAutenticacion servicio = new ServicioAutenticacion();
            servicio.autenticar("usuario_que_no_existe", "clave_incorrecta");
        });

        try {
            LoginControlador controlador = new LoginControlador();
            for (int i = 0; i < 3; i++) {
                try {
                    controlador.intentarLogin("usuario_que_no_existe", "clave_incorrecta");
                } catch (AutenticacionFallidaException ignorado) {
                    // esperado en cada uno de los 3 intentos
                }
            }
            if (controlador.bloqueado()) {
                casosOk++;
                System.out.println("[OK  ] Bloqueo tras 3 intentos fallidos - el controlador quedó bloqueado.");
            } else {
                casosFallidos++;
                System.out.println("[MAL ] Bloqueo tras 3 intentos fallidos - el controlador NO quedó bloqueado.");
            }
        } catch (Exception e) {
            casosFallidos++;
            System.out.println("[MAL ] Bloqueo tras 3 intentos fallidos - error inesperado: " + e.getMessage());
        }
    }

    private static void probarEmpleadoDatosInvalidos() {
        esperarExcepcion("Registrar empleado con correo inválido", ValidacionException.class, () -> {
            EmpleadoDAO dao = new EmpleadoDAO();
            Empleado e = new Empleado();
            e.setCedula("1-1111-1111");
            e.setNombreCompleto("Empleado de prueba");
            e.setPuesto("Prueba");
            e.setSalarioBase(500000);
            e.setCorreo("esto-no-es-un-correo");
            dao.crear(e);
        });

        esperarExcepcion("Registrar empleado con salario en cero", ValidacionException.class, () -> {
            EmpleadoDAO dao = new EmpleadoDAO();
            Empleado e = new Empleado();
            e.setCedula("1-1111-1112");
            e.setNombreCompleto("Empleado de prueba 2");
            e.setPuesto("Prueba");
            e.setSalarioBase(0);
            e.setCorreo("valido@ejemplo.com");
            dao.crear(e);
        });
    }

    private static void probarActualizarRegistroInexistente() {
        esperarExcepcion("Actualizar nómina con id inexistente", ArchivoInvalidoException.class, () -> {
            NominaDAO dao = new NominaDAO();
            Nomina n = new Nomina();
            n.setId(999999);
            n.setIdEmpleado(1);
            n.setPeriodo("2026-01");
            dao.actualizar(n);
        });
    }

    private static void probarEliminarRegistroInexistente() {
        esperarExcepcion("Eliminar detalle de nómina con id inexistente", ArchivoInvalidoException.class, () -> {
            DetalleNominaDAO dao = new DetalleNominaDAO();
            dao.eliminarPorId(999999);
        });
    }

    private static void probarCalculoDatosInvalidos() {
        esperarExcepcion("Calcular nómina con salario base en cero", CalculoNominaException.class, () -> {
            CalculoNominaCR calculadora = new CalculoNominaCR();
            Empleado e = new Empleado();
            e.setId(1);
            e.setSalarioBase(0);
            calculadora.calcular(e, "2026-08");
        });

        esperarExcepcion("Calcular nómina sin período", CalculoNominaException.class, () -> {
            CalculoNominaCR calculadora = new CalculoNominaCR();
            Empleado e = new Empleado();
            e.setId(1);
            e.setSalarioBase(500000);
            calculadora.calcular(e, "");
        });
    }

    private static void esperarExcepcion(String nombreCaso, Class<? extends Exception> tipoEsperado, Accion accion) {
        try {
            accion.ejecutar();
            casosFallidos++;
            System.out.println("[MAL ] " + nombreCaso + " - se esperaba " + tipoEsperado.getSimpleName()
                    + " pero no se lanzó ninguna excepción.");
        } catch (Exception e) {
            if (tipoEsperado.isInstance(e)) {
                casosOk++;
                System.out.println("[OK  ] " + nombreCaso + " - lanzó " + e.getClass().getSimpleName()
                        + ": " + e.getMessage());
            } else {
                casosFallidos++;
                System.out.println("[MAL ] " + nombreCaso + " - se esperaba " + tipoEsperado.getSimpleName()
                        + " pero se lanzó " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
