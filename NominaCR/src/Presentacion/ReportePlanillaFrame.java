package Presentacion;

import Excepciones.CorreoException;
import Excepciones.ReporteException;
import Presentacion.controladores.NominaControlador;
import Reportes.DatosColilla;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Pantalla para generar y enviar el resumen de planilla al patrono
 * (Etapa 5), agrupando todas las nóminas guardadas de un período. No
 * calcula ni construye el PDF por su cuenta: delega todo a
 * {@link NominaControlador}.
 */
public class ReportePlanillaFrame extends JFrame {

    private final NominaControlador controlador;
    private final JTextField campoPeriodo = new JTextField(10);
    private final JLabel etiquetaEstado = new JLabel(" ");
    private File pdfGenerado;

    public ReportePlanillaFrame() {
        super("Resumen de Planilla (Patrono)");

        NominaControlador controladorTemp = null;
        try {
            controladorTemp = new NominaControlador();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo iniciar el módulo de reportes: " + e.getMessage(),
                    "Error crítico", JOptionPane.ERROR_MESSAGE);
        }
        this.controlador = controladorTemp;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Período (ej. 2026-08):"));
        panelSuperior.add(campoPeriodo);

        JButton botonExportar = new JButton("Exportar PDF...");
        botonExportar.addActionListener(e -> onExportar());
        panelSuperior.add(botonExportar);

        JButton botonEnviar = new JButton("Enviar al patrono");
        botonEnviar.addActionListener(e -> onEnviar());
        panelSuperior.add(botonEnviar);

        add(panelSuperior, BorderLayout.NORTH);
        add(etiquetaEstado, BorderLayout.SOUTH);

        setSize(500, 150);
        setLocationRelativeTo(null);
    }

    private void onExportar() {
        if (controlador == null) {
            return;
        }
        String periodo = campoPeriodo.getText().trim();
        if (periodo.isEmpty()) {
            mostrarError("Indique el período.");
            return;
        }

        try {
            List<DatosColilla> datos = controlador.listarDatosPlanillaPorPeriodo(periodo);
            if (datos.isEmpty()) {
                mostrarError("No hay nóminas guardadas para el período " + periodo + ".");
                return;
            }

            JFileChooser selector = new JFileChooser();
            selector.setSelectedFile(new File("resumen_planilla_" + periodo + ".pdf"));
            int opcion = selector.showSaveDialog(this);
            if (opcion != JFileChooser.APPROVE_OPTION) {
                return;
            }

            pdfGenerado = controlador.generarResumenPlanillaPDF(datos, selector.getSelectedFile().getAbsolutePath());
            etiquetaEstado.setText("PDF generado: " + pdfGenerado.getAbsolutePath());
        } catch (ReporteException e) {
            mostrarError("No se pudo generar el PDF: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void onEnviar() {
        if (controlador == null) {
            return;
        }
        String periodo = campoPeriodo.getText().trim();
        if (periodo.isEmpty()) {
            mostrarError("Indique el período.");
            return;
        }
        if (pdfGenerado == null) {
            mostrarError("Primero exporte el PDF del resumen con el botón \"Exportar PDF...\".");
            return;
        }

        try {
            controlador.enviarResumenPorCorreo(periodo, pdfGenerado);
            etiquetaEstado.setText("Correo enviado al patrono correctamente.");
            JOptionPane.showMessageDialog(this, "Correo enviado correctamente.");
        } catch (CorreoException e) {
            mostrarError("No se pudo enviar el correo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
