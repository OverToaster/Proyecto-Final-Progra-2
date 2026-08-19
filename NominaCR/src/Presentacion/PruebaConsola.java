package Presentacion;

import AccesoDatos.archivo.DetalleNominaDAO;
import AccesoDatos.archivo.EmpleadoDAO;
import AccesoDatos.archivo.NominaDAO;
import AccesoDatos.archivo.UsuarioDAO;
import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Entidades.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de prueba en consola para la Etapa 2 del proyecto: demuestra que
 * las entidades y la capa de acceso a datos funcionan correctamente
 * (crear, cargar por id, actualizar, eliminar, listar) antes de construir
 * la interfaz gráfica.
 *
 * Se puede ejecutar directamente (tiene su propio main) sin afectar la
 * futura clase Main de arranque de la aplicación con Swing.
 */
public class PruebaConsola {

    public static void main(String[] args) {
        try {
            probarUsuarios();
            probarEmpleados();
            probarNominaYDetalle();
            System.out.println("\n=== Todas las pruebas de la Etapa 2 finalizaron sin errores ===");
        } catch (Exception e) {
            System.out.println("ERROR durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarUsuarios() throws Exception {
        System.out.println("--- Prueba UsuarioDAO ---");
        UsuarioDAO dao = new UsuarioDAO();

        Usuario u = new Usuario();
        u.setUsername("bvega");
        u.setPassword("clave123");
        u.setRol("ADMIN");
        dao.crear(u);
        System.out.println("Creado: " + u);

        Usuario buscado = new Usuario();
        buscado.setId(u.getId());
        dao.cargarPorId(buscado);
        System.out.println("Cargado por id: " + buscado);

        buscado.setRol("SUPERVISOR");
        dao.actualizar(buscado);
        System.out.println("Actualizado: " + buscado);

        List<Usuario> todos = new ArrayList<>();
        dao.listar(todos);
        System.out.println("Total usuarios en archivo: " + todos.size());
    }

    private static void probarEmpleados() throws Exception {
        System.out.println("\n--- Prueba EmpleadoDAO ---");
        EmpleadoDAO dao = new EmpleadoDAO();

        Empleado e = new Empleado();
        e.setCedula("1-2345-6789");
        e.setNombreCompleto("Ana Rodríguez Solano");
        e.setPuesto("Analista");
        e.setSalarioBase(650000);
        e.setHorasExtra(4);
        e.setCorreo("ana.rodriguez@ejemplo.com");
        dao.crear(e);
        System.out.println("Creado: " + e);

        List<Empleado> todos = new ArrayList<>();
        dao.listar(todos);
        System.out.println("Total empleados en archivo: " + todos.size());

        dao.eliminarPorId(e.getId());
        System.out.println("Empleado id=" + e.getId() + " eliminado correctamente.");
    }

    private static void probarNominaYDetalle() throws Exception {
        System.out.println("\n--- Prueba NominaDAO / DetalleNominaDAO ---");
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        NominaDAO nominaDAO = new NominaDAO();
        DetalleNominaDAO detalleDAO = new DetalleNominaDAO();

        Empleado emp = new Empleado();
        emp.setCedula("2-3456-7890");
        emp.setNombreCompleto("Carlos Jiménez Mora");
        emp.setPuesto("Desarrollador");
        emp.setSalarioBase(800000);
        emp.setCorreo("carlos.jimenez@ejemplo.com");
        empleadoDAO.crear(emp);

        Nomina n = new Nomina();
        n.setIdEmpleado(emp.getId());
        n.setPeriodo("2026-08");
        n.setSalarioBruto(800000);
        n.setTotalDeduccionesTrabajador(85360);
        n.setImpuestoRenta(0);
        n.setSalarioNeto(714640);
        n.setTotalAportesPatronales(212800);
        n.setFechaGeneracion("2026-08-08");
        nominaDAO.crear(n);
        System.out.println("Nómina creada: " + n);

        DetalleNomina d1 = new DetalleNomina();
        d1.setIdNomina(n.getId());
        d1.setConcepto("CCSS SEM");
        d1.setMonto(44000);
        d1.setTipo(DetalleNomina.TipoRubro.DEDUCCION_TRABAJADOR);
        detalleDAO.crear(d1);

        DetalleNomina d2 = new DetalleNomina();
        d2.setIdNomina(n.getId());
        d2.setConcepto("CCSS IVM");
        d2.setMonto(33360);
        d2.setTipo(DetalleNomina.TipoRubro.DEDUCCION_TRABAJADOR);
        detalleDAO.crear(d2);

        List<DetalleNomina> detalle = detalleDAO.listarPorNomina(n.getId());
        System.out.println("Líneas de detalle para la nómina id=" + n.getId() + ": " + detalle.size());
        for (DetalleNomina d : detalle) {
            System.out.println("  " + d);
        }

        // limpieza de los datos de prueba
        detalleDAO.eliminarPorId(d1.getId());
        detalleDAO.eliminarPorId(d2.getId());
        nominaDAO.eliminarPorId(n.getId());
        empleadoDAO.eliminarPorId(emp.getId());
        System.out.println("Datos de prueba de nómina limpiados correctamente.");
    }
}
