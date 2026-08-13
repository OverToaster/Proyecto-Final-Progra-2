package Presentacion;

import Entidades.Empleado;
import Excepciones.ArchivoInvalidoException;
import Excepciones.ValidacionException;
import Presentacion.controladores.EmpleadoControlador;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla de gestión de empleados: registrar, consultar, modificar y
 * eliminar. Toda la validación de reglas de negocio vive en EmpleadoDAO
 * (Etapa 2); aquí solo se valida que los campos numéricos tengan un
 * formato parseable, porque eso es estrictamente un problema de la
 * interfaz (texto -> número), no una regla de negocio.
 */
public class EmpleadoFrame extends JFrame {

    private final EmpleadoControlador controlador;

    private final JTextField campoCedula = new JTextField(12);
    private final JTextField campoNombre = new JTextField(20);
    private final JTextField campoPuesto = new JTextField(15);
    private final JTextField campoSalario = new JTextField(10);
    private final JTextField campoHorasExtra = new JTextField(6);
    private final JTextField campoCorreo = new JTextField(20);
    private final JCheckBox campoActivo = new JCheckBox("Activo", true);

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private Integer idSeleccionado = null;

    public EmpleadoFrame() {
        super("Gestión de Empleados");

        EmpleadoControlador controladorTemp = null;
        try {
            controladorTemp = new EmpleadoControlador();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo iniciar la gestión de empleados: " + e.getMessage(),
                    "Error crítico", JOptionPane.ERROR_MESSAGE);
        }
        this.controlador = controladorTemp;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Cédula", "Nombre", "Puesto", "Salario base", "Horas extra", "Correo", "Activo"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(construirPanelFormulario(), BorderLayout.SOUTH);

        cargarTabla();

        setSize(820, 520);
        setLocationRelativeTo(null);
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(panel, gbc, fila++, "Cédula:", campoCedula);
        agregarCampo(panel, gbc, fila++, "Nombre completo:", campoNombre);
        agregarCampo(panel, gbc, fila++, "Puesto:", campoPuesto);
        agregarCampo(panel, gbc, fila++, "Salario base:", campoSalario);
        agregarCampo(panel, gbc, fila++, "Horas extra:", campoHorasExtra);
        agregarCampo(panel, gbc, fila++, "Correo:", campoCorreo);

        gbc.gridx = 1;
        gbc.gridy = fila++;
        gbc.gridwidth = 1;
        panel.add(campoActivo, gbc);

        JPanel panelBotones = new JPanel();
        JButton botonRegistrar = new JButton("Registrar");
        JButton botonModificar = new JButton("Modificar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");

        botonRegistrar.addActionListener(e -> onRegistrar());
        botonModificar.addActionListener(e -> onModificar());
        botonEliminar.addActionListener(e -> onEliminar());
        botonLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonModificar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void cargarTabla() {
        if (controlador == null) {
            return;
        }
        try {
            modeloTabla.setRowCount(0);
            List<Empleado> empleados = controlador.listarEmpleados();
            for (Empleado emp : empleados) {
                modeloTabla.addRow(new Object[]{
                        emp.getId(), emp.getCedula(), emp.getNombreCompleto(), emp.getPuesto(),
                        emp.getSalarioBase(), emp.getHorasExtra(), emp.getCorreo(), emp.isActivo()
                });
            }
        } catch (Exception e) {
            mostrarError("No se pudo cargar la lista de empleados: " + e.getMessage());
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (Integer) modeloTabla.getValueAt(fila, 0);
        campoCedula.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        campoNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
        campoPuesto.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
        campoSalario.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
        campoHorasExtra.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));
        campoCorreo.setText(String.valueOf(modeloTabla.getValueAt(fila, 6)));
        campoActivo.setSelected((Boolean) modeloTabla.getValueAt(fila, 7));
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoCedula.setText("");
        campoNombre.setText("");
        campoPuesto.setText("");
        campoSalario.setText("");
        campoHorasExtra.setText("");
        campoCorreo.setText("");
        campoActivo.setSelected(true);
        tabla.clearSelection();
    }

    private Empleado leerFormulario() {
        Empleado emp = new Empleado();
        if (idSeleccionado != null) {
            emp.setId(idSeleccionado);
        }
        emp.setCedula(campoCedula.getText().trim());
        emp.setNombreCompleto(campoNombre.getText().trim());
        emp.setPuesto(campoPuesto.getText().trim());

        try {
            String textoSalario = campoSalario.getText().trim();
            emp.setSalarioBase(textoSalario.isEmpty() ? 0 : Double.parseDouble(textoSalario));
        } catch (NumberFormatException e) {
            throw new ValidacionException("El salario base debe ser un número (ej. 650000 o 650000.50).");
        }

        try {
            String textoHoras = campoHorasExtra.getText().trim();
            emp.setHorasExtra(textoHoras.isEmpty() ? 0 : Double.parseDouble(textoHoras));
        } catch (NumberFormatException e) {
            throw new ValidacionException("Las horas extra deben ser un número (ej. 4 o 4.5).");
        }

        emp.setCorreo(campoCorreo.getText().trim());
        emp.setActivo(campoActivo.isSelected());
        return emp;
    }

    private void onRegistrar() {
        if (controlador == null) {
            return;
        }
        try {
            Empleado emp = leerFormulario();
            emp.setId(0); // fuerza a que el DAO le asigne un id nuevo, aunque haya uno seleccionado en la tabla
            controlador.registrar(emp);
            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        } catch (ArchivoInvalidoException e) {
            mostrarError("Error de archivo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void onModificar() {
        if (controlador == null) {
            return;
        }
        if (idSeleccionado == null) {
            mostrarError("Seleccione primero un empleado de la tabla para modificarlo.");
            return;
        }
        try {
            Empleado emp = leerFormulario();
            controlador.modificar(emp);
            JOptionPane.showMessageDialog(this, "Empleado modificado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        } catch (ArchivoInvalidoException e) {
            mostrarError("Error de archivo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void onEliminar() {
        if (controlador == null) {
            return;
        }
        if (idSeleccionado == null) {
            mostrarError("Seleccione primero un empleado de la tabla para eliminarlo.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este empleado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controlador.eliminar(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Empleado eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (ArchivoInvalidoException e) {
            mostrarError("Error de archivo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
