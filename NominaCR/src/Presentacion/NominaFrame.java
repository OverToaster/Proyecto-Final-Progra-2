package Presentacion;

import Entidades.DetalleNomina;
import Entidades.Empleado;
import Entidades.Nomina;
import Excepciones.CalculoNominaException;
import Excepciones.CorreoException;
import Excepciones.ReporteException;
import Logica.ResultadoCalculo;
import Presentacion.controladores.NominaControlador;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla de generación de nómina: selecciona empleado y período,
 * ejecuta el cálculo (delegado por completo a la capa de lógica) y
 * muestra el desglose completo. No contiene ninguna fórmula de cálculo.
 */
public class NominaFrame extends JFrame {

    private final NominaControlador controlador;
    private final JComboBox<Empleado> comboEmpleados = new JComboBox<>();
    private final JTextField campoPeriodo = new JTextField(10);
    private final DefaultTableModel modeloDesglose;
    private final JTable tablaDesglose;
    private final JLabel etiquetaBruto = new JLabel("₡0.00");
    private final JLabel etiquetaDeducciones = new JLabel("₡0.00");
    private final JLabel etiquetaRenta = new JLabel("₡0.00");
    private final JLabel etiquetaNeto = new JLabel("₡0.00");
    private final JLabel etiquetaAportes = new JLabel("₡0.00");
    private final JButton botonGuardar = new JButton("Guardar nómina");
    private final JButton botonExportar = new JButton("Exportar PDF...");
    private final JButton botonEnviarCorreo = new JButton("Enviar por correo");

    private ResultadoCalculo resultadoActual;
    private Empleado empleadoDelResultado;
    private File pdfGenerado;

    public NominaFrame() {
        super("Generar Nómina");

        NominaControlador controladorTemp = null;
        try {
            controladorTemp = new NominaControlador();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo iniciar el módulo de nómina: " + e.getMessage(),
                    "Error crítico", JOptionPane.ERROR_MESSAGE);
        }
        this.controlador = controladorTemp;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        configurarComboEmpleados();
        add(construirPanelSuperior(), BorderLayout.NORTH);

        modeloDesglose = new DefaultTableModel(new Object[]{"Concepto", "Tipo", "Monto"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaDesglose = new JTable(modeloDesglose);
        add(new JScrollPane(tablaDesglose), BorderLayout.CENTER);

        add(construirPanelResumen(), BorderLayout.SOUTH);

        cargarEmpleados();
        botonGuardar.setEnabled(false);
        botonExportar.setEnabled(false);
        botonEnviarCorreo.setEnabled(false);

        setSize(720, 520);
        setLocationRelativeTo(null);
    }

    private void configurarComboEmpleados() {
        comboEmpleados.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean tieneFoco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, tieneFoco);
                if (valor instanceof Empleado emp) {
                    setText(emp.getNombreCompleto() + " (" + emp.getCedula() + ")");
                }
                return this;
            }
        });
    }

    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Empleado:"));
        panel.add(comboEmpleados);
        panel.add(new JLabel("Período (ej. 2026-08):"));
        panel.add(campoPeriodo);

        JButton botonCalcular = new JButton("Calcular");
        botonCalcular.addActionListener(e -> onCalcular());
        panel.add(botonCalcular);

        botonGuardar.addActionListener(e -> onGuardar());
        panel.add(botonGuardar);

        botonExportar.addActionListener(e -> onExportar());
        panel.add(botonExportar);

        botonEnviarCorreo.addActionListener(e -> onEnviarCorreo());
        panel.add(botonEnviarCorreo);

        return panel;
    }

    private JPanel construirPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));
        panel.add(construirEtiquetaResumen("Salario bruto", etiquetaBruto));
        panel.add(construirEtiquetaResumen("Deducciones", etiquetaDeducciones));
        panel.add(construirEtiquetaResumen("Renta", etiquetaRenta));
        panel.add(construirEtiquetaResumen("Salario neto", etiquetaNeto));
        panel.add(construirEtiquetaResumen("Aportes patronales", etiquetaAportes));
        return panel;
    }

    private JPanel construirEtiquetaResumen(String titulo, JLabel etiquetaValor) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel etiquetaTitulo = new JLabel(titulo, SwingConstants.CENTER);
        etiquetaTitulo.setFont(etiquetaTitulo.getFont().deriveFont(Font.BOLD, 11f));
        etiquetaValor.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(etiquetaTitulo);
        panel.add(etiquetaValor);
        return panel;
    }

    private void cargarEmpleados() {
        if (controlador == null) {
            return;
        }
        try {
            List<Empleado> empleados = controlador.listarEmpleadosActivos();
            comboEmpleados.removeAllItems();
            for (Empleado e : empleados) {
                comboEmpleados.addItem(e);
            }
        } catch (Exception e) {
            mostrarError("No se pudo cargar la lista de empleados: " + e.getMessage());
        }
    }

    private void onCalcular() {
        if (controlador == null) {
            return;
        }
        Empleado seleccionado = (Empleado) comboEmpleados.getSelectedItem();
        String periodo = campoPeriodo.getText().trim();

        if (seleccionado == null) {
            mostrarError("Seleccione un empleado (registre empleados activos primero si la lista está vacía).");
            return;
        }

        try {
            resultadoActual = controlador.calcularNomina(seleccionado, periodo);
            empleadoDelResultado = seleccionado;
            pdfGenerado = null;
            mostrarResultado(resultadoActual);
            botonGuardar.setEnabled(true);
            botonExportar.setEnabled(false);
            botonEnviarCorreo.setEnabled(false);
        } catch (CalculoNominaException e) {
            mostrarError(e.getMessage());
            botonGuardar.setEnabled(false);
        } catch (Exception e) {
            mostrarError("Error inesperado al calcular: " + e.getMessage());
            botonGuardar.setEnabled(false);
        }
    }

    private void mostrarResultado(ResultadoCalculo resultado) {
        Nomina n = resultado.getNomina();
        modeloDesglose.setRowCount(0);
        for (DetalleNomina d : resultado.getDetalle()) {
            modeloDesglose.addRow(new Object[]{d.getConcepto(), d.getTipo(), formatoMonto(d.getMonto())});
        }
        etiquetaBruto.setText(formatoMonto(n.getSalarioBruto()));
        etiquetaDeducciones.setText(formatoMonto(n.getTotalDeduccionesTrabajador()));
        etiquetaRenta.setText(formatoMonto(n.getImpuestoRenta()));
        etiquetaNeto.setText(formatoMonto(n.getSalarioNeto()));
        etiquetaAportes.setText(formatoMonto(n.getTotalAportesPatronales()));
    }

    private String formatoMonto(double monto) {
        return String.format("₡%,.2f", monto);
    }

    private void onGuardar() {
        if (controlador == null || resultadoActual == null) {
            return;
        }
        try {
            controlador.guardarNomina(resultadoActual);
            JOptionPane.showMessageDialog(this, "Nómina guardada correctamente.");
            botonGuardar.setEnabled(false);
            botonExportar.setEnabled(true);
            botonEnviarCorreo.setEnabled(true);
        } catch (Exception e) {
            mostrarError("No se pudo guardar la nómina: " + e.getMessage());
        }
    }

    private void onExportar() {
        if (controlador == null || resultadoActual == null || empleadoDelResultado == null) {
            return;
        }
        JFileChooser selector = new JFileChooser();
        String nombreSugerido = "colilla_" + empleadoDelResultado.getCedula().replaceAll("[^0-9A-Za-z]", "")
                + "_" + resultadoActual.getNomina().getPeriodo() + ".pdf";
        selector.setSelectedFile(new File(nombreSugerido));
        int opcion = selector.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            pdfGenerado = controlador.generarColillaPDF(
                    empleadoDelResultado, resultadoActual, selector.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "PDF generado en: " + pdfGenerado.getAbsolutePath());
        } catch (ReporteException e) {
            mostrarError("No se pudo generar el PDF: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado al generar el PDF: " + e.getMessage());
        }
    }

    private void onEnviarCorreo() {
        if (controlador == null || resultadoActual == null || empleadoDelResultado == null) {
            return;
        }
        if (pdfGenerado == null) {
            mostrarError("Primero exporte el PDF con el botón \"Exportar PDF...\".");
            return;
        }

        try {
            controlador.enviarColillaPorCorreo(
                    empleadoDelResultado, resultadoActual.getNomina().getPeriodo(), pdfGenerado);
            JOptionPane.showMessageDialog(this,
                    "Correo enviado a " + empleadoDelResultado.getCorreo() + " correctamente.");
        } catch (CorreoException e) {
            mostrarError("No se pudo enviar el correo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado al enviar el correo: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
