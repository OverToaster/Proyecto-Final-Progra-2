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
import javax.swing.SwingUtilities;

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

        JButton botonRegistrar = new JButton("Registrar usuario");
        gbc.gridy = 3;
        add(botonRegistrar, gbc);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setForeground(Color.RED);
        gbc.gridy = 4;
        add(etiquetaMensaje, gbc);

        botonIngresar.addActionListener(this::onIngresar);
        campoPassword.addActionListener(this::onIngresar);
        botonRegistrar.addActionListener(e -> abrirRegistro());

        pack();
        setLocationRelativeTo(null);

        verificarSiHayUsuarios();
    }

    /**
     * Si usuarios.txt está vacío (primera vez que se usa el sistema),
     * avisa y abre automáticamente el registro para crear el primer
     * usuario. Si la verificación falla por cualquier motivo, el login
     * sigue funcionando normalmente (solo no se ofrece el registro
     * automático).
     */
    private void verificarSiHayUsuarios() {
        if (controlador == null) {
            return;
        }
        try {
            if (!controlador.hayUsuariosRegistrados()) {
                etiquetaMensaje.setForeground(new Color(0, 90, 190));
                etiquetaMensaje.setText("No hay usuarios registrados. Registre el primero.");
                SwingUtilities.invokeLater(this::abrirRegistro);
            }
        } catch (Exception e) {
            // No se pudo verificar; se deja el login disponible igual.
        }
    }

    private void abrirRegistro() {
        if (controlador == null) {
            return;
        }
        RegistroUsuarioFrame dialogo = new RegistroUsuarioFrame(this, controlador);
        dialogo.setVisible(true);
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
