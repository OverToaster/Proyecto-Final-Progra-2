package Presentacion;

import Entidades.Usuario;
import Excepciones.ValidacionException;
import Presentacion.controladores.LoginControlador;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Ventana de registro de un nuevo usuario del sistema (Etapa 4). Se
 * muestra desde {@link LoginFrame}, tanto de forma manual (botón
 * "Registrar usuario") como automática cuando usuarios.txt está vacío.
 * No valida reglas de negocio por su cuenta: solo lee el formulario y
 * delega todo a {@link LoginControlador}.
 */
public class RegistroUsuarioFrame extends JDialog {

    private final LoginControlador controlador;
    private final JTextField campoUsuario = new JTextField(15);
    private final JPasswordField campoPassword = new JPasswordField(15);
    private final JPasswordField campoConfirmar = new JPasswordField(15);
    private final JComboBox<String> comboRol = new JComboBox<>(new String[]{"Patrono", "RRHH"});

    private boolean registroExitoso = false;

    public RegistroUsuarioFrame(JFrame propietario, LoginControlador controlador) {
        super(propietario, "Registrar usuario", true);
        this.controlador = controlador;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(gbc, fila++, "Usuario:", campoUsuario);
        agregarCampo(gbc, fila++, "Contraseña:", campoPassword);
        agregarCampo(gbc, fila++, "Confirmar contraseña:", campoConfirmar);
        agregarCampo(gbc, fila++, "Rol:", comboRol);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(e -> onRegistrar());

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        add(botonRegistrar, gbc);

        pack();
        setLocationRelativeTo(propietario);
    }

    private void agregarCampo(GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    private void onRegistrar() {
        if (controlador == null) {
            return;
        }

        String username = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        if (!password.equals(confirmar)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario nuevo = controlador.registrarUsuario(username, password, rol);
            registroExitoso = true;
            JOptionPane.showMessageDialog(this,
                    "Usuario \"" + nuevo.getUsername() + "\" registrado correctamente. Ya puede iniciar sesión.");
            dispose();
        } catch (ValidacionException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistroExitoso() {
        return registroExitoso;
    }
}
