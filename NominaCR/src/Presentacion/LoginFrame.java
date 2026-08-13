package Presentacion;

import Entidades.Usuario;
import Excepciones.AutenticacionFallidaException;
import Presentacion.controladores.LoginControlador;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Pantalla de inicio de sesión. Solo dibuja la interfaz y delega toda
 * decisión (validar credenciales, contar intentos, bloquear) a
 * {@link LoginControlador}.
 */
public class LoginFrame extends JFrame {

    private final JTextField campoUsuario;
    private final JPasswordField campoPassword;
    private final JLabel etiquetaMensaje;
    private final LoginControlador controlador;

    public LoginFrame() {
        super("Sistema de Nómina - Inicio de sesión");

        LoginControlador controladorTemp;
        try {
            controladorTemp = new LoginControlador();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo iniciar el módulo de autenticación: " + e.getMessage(),
                    "Error crítico", JOptionPane.ERROR_MESSAGE);
            controladorTemp = null;
        }
        this.controlador = controladorTemp;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        campoUsuario = new JTextField(15);
        add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        campoPassword = new JPasswordField(15);
        add(campoPassword, gbc);

        JButton botonIngresar = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(botonIngresar, gbc);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setForeground(Color.RED);
        gbc.gridy = 3;
        add(etiquetaMensaje, gbc);

        botonIngresar.addActionListener(this::onIngresar);
        campoPassword.addActionListener(this::onIngresar);

        pack();
        setLocationRelativeTo(null);
    }

    private void onIngresar(ActionEvent evento) {
        if (controlador == null) {
            return;
        }
        if (controlador.bloqueado()) {
            etiquetaMensaje.setText("Cuenta bloqueada. Cierre y vuelva a abrir el sistema.");
            return;
        }

        String usuario = campoUsuario.getText();
        String password = new String(campoPassword.getPassword());

        try {
            Usuario usuarioAutenticado = controlador.intentarLogin(usuario, password);
            etiquetaMensaje.setForeground(new Color(0, 128, 0));
            etiquetaMensaje.setText("Bienvenido, " + usuarioAutenticado.getUsername());
            dispose();
            new MainFrame(usuarioAutenticado).setVisible(true);
        } catch (AutenticacionFallidaException e) {
            etiquetaMensaje.setForeground(Color.RED);
            if (controlador.bloqueado()) {
                JOptionPane.showMessageDialog(this,
                        "Se alcanzó el máximo de " + LoginControlador.getMaxIntentos()
                        + " intentos fallidos. El sistema se cerrará.",
                        "Acceso bloqueado", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                etiquetaMensaje.setText(e.getMessage() + " (intentos restantes: "
                        + controlador.getIntentosRestantes() + ")");
            }
        } catch (Exception e) {
            etiquetaMensaje.setForeground(Color.RED);
            etiquetaMensaje.setText("Error inesperado: " + e.getMessage());
        }
    }
}
