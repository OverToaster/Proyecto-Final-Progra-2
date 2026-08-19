package Presentacion;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación de escritorio. Arranca mostrando la
 * pantalla de login; toda la navegación posterior (menú principal,
 * gestión de empleados, generación de nómina) se dispara desde ahí.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
