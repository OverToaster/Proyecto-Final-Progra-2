package Presentacion.controladores;

import AccesoDatos.archivo.DetalleNominaDAO;
import AccesoDatos.archivo.EmpleadoDAO;
import AccesoDatos.archivo.NominaDAO;
import Correo.ServicioCorreo;
import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.CorreoException;
import Excepciones.ReporteException;
import Logica.CalculoNominaCR;
import Logica.ResultadoCalculo;
import Reportes.DatosColilla;
import Reportes.IGeneradorReporte;
import Reportes.ReporteEmpleadoPDF;
import Reportes.ReportePatronoPDF;
import Utilidades.ConfiguracionCorreo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la pantalla de generación de nómina. No calcula, no
 * arma PDFs ni envía correos por su cuenta: delega el cálculo a
 * Logica.CalculoNominaCR, la persistencia a los DAO de la Etapa 2, la
 * generación de reportes a la capa Reportes y el envío a la capa Correo
 * (ambas de la Etapa 5).
 */
public class NominaControlador {

    private final EmpleadoDAO empleadoDAO;
    private final NominaDAO nominaDAO;
    private final DetalleNominaDAO detalleDAO;
    private final CalculoNominaCR calculadora;

    public NominaControlador() throws IOException {
        this.empleadoDAO = new EmpleadoDAO();
        this.nominaDAO = new NominaDAO();
        this.detalleDAO = new DetalleNominaDAO();
        this.calculadora = new CalculoNominaCR();
    }

    public List<Empleado> listarEmpleadosActivos() throws IOException {
        List<Empleado> todos = new ArrayList<>();
        empleadoDAO.listar(todos);
        List<Empleado> activos = new ArrayList<>();
        for (Empleado e : todos) {
            if (e.isActivo()) {
                activos.add(e);
            }
        }
        return activos;
    }

    public ResultadoCalculo calcularNomina(Empleado empleado, String periodo) {
        return calculadora.calcular(empleado, periodo);
    }

    public void guardarNomina(ResultadoCalculo resultado) throws IOException {
        nominaDAO.crear(resultado.getNomina());
        for (DetalleNomina d : resultado.getDetalle()) {
            d.setIdNomina(resultado.getNomina().getId());
            detalleDAO.crear(d);
        }
    }

    // ---------- Etapa 5: reportes PDF y correo ----------

    /**
     * Genera la colilla de pago en PDF para un empleado y su resultado
     * de nómina ya calculado, en la ruta que el usuario haya elegido.
     */
    public File generarColillaPDF(Empleado empleado, ResultadoCalculo resultado, String rutaDestino)
            throws ReporteException {
        DatosColilla datos = new DatosColilla(empleado, resultado.getNomina(), resultado.getDetalle());
        IGeneradorReporte<DatosColilla> generador = new ReporteEmpleadoPDF();
        return generador.generar(datos, rutaDestino);
    }

    /**
     * Envía la colilla de pago (ya generada como PDF) al correo del
     * empleado.
     */
    public void enviarColillaPorCorreo(Empleado empleado, String periodo, File pdfColilla) throws CorreoException {
        ServicioCorreo servicio = new ServicioCorreo(
                ConfiguracionCorreo.CORREO_REMITENTE, ConfiguracionCorreo.CORREO_CLAVE,
                ConfiguracionCorreo.SERVIDOR_SMTP, ConfiguracionCorreo.PUERTO_SMTP);

        String asunto = "Colilla de pago - " + periodo;
        String cuerpo = "Estimado/a " + empleado.getNombreCompleto() + ",\n\n"
                + "Adjunto encontrará su colilla de pago correspondiente al período " + periodo + ".\n\n"
                + "Saludos,\nDepartamento de Recursos Humanos";

        servicio.enviarConAdjunto(empleado.getCorreo(), asunto, cuerpo, pdfColilla);
    }

    /**
     * Reúne todas las nóminas guardadas de un período, junto con sus
     * empleados y detalle, listas para armar el resumen de planilla.
     */
    public List<DatosColilla> listarDatosPlanillaPorPeriodo(String periodo) throws IOException {
        List<Nomina> nominas = nominaDAO.listarPorPeriodo(periodo);
        List<DatosColilla> resultado = new ArrayList<>();
        for (Nomina n : nominas) {
            Empleado emp = new Empleado();
            emp.setId(n.getIdEmpleado());
            empleadoDAO.cargarPorId(emp);
            List<DetalleNomina> detalle = detalleDAO.listarPorNomina(n.getId());
            resultado.add(new DatosColilla(emp, n, detalle));
        }
        return resultado;
    }

    /**
     * Genera el PDF de resumen de planilla del patrono para un período.
     */
    public File generarResumenPlanillaPDF(List<DatosColilla> datos, String rutaDestino) throws ReporteException {
        IGeneradorReporte<List<DatosColilla>> generador = new ReportePatronoPDF();
        return generador.generar(datos, rutaDestino);
    }

    /**
     * Envía el resumen de planilla (ya generado como PDF) al correo del
     * patrono configurado en Utilidades.ConfiguracionCorreo.
     */
    public void enviarResumenPorCorreo(String periodo, File pdfResumen) throws CorreoException {
        ServicioCorreo servicio = new ServicioCorreo(
                ConfiguracionCorreo.CORREO_REMITENTE, ConfiguracionCorreo.CORREO_CLAVE,
                ConfiguracionCorreo.SERVIDOR_SMTP, ConfiguracionCorreo.PUERTO_SMTP);

        String asunto = "Resumen de planilla - " + periodo;
        String cuerpo = "Se adjunta el resumen de la planilla correspondiente al período " + periodo + ".";

        servicio.enviarConAdjunto(ConfiguracionCorreo.CORREO_PATRONO, asunto, cuerpo, pdfResumen);
    }
}
