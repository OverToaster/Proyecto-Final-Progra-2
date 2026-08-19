package Presentacion;

import Entidades.Usuario;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

/**
 * Menú principal de la aplicación, mostrado después de un login exitoso.
 * Solo arma la navegación hacia las demás pantallas; no contiene lógica
 * propia.
 */
public class MainFrame extends JFrame {

    public MainFrame(Usuario usuarioActivo) {
        super("Sistema de Nómina - " + usuarioActivo.getUsername() + " (" + usuarioActivo.getRol() + ")");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(construirMenu());

        JLabel bienvenida = new JLabel(
                "Bienvenido/a, " + usuarioActivo.getUsername()
                + ". Use el menú \"Gestión\" para administrar empleados o generar una nómina.",
                SwingConstants.CENTER);
        add(bienvenida, BorderLayout.CENTER);

        setSize(520, 200);
        setLocationRelativeTo(null);
    }

    private JMenuBar construirMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuGestion = new JMenu("Gestión");

        JMenuItem itemEmpleados = new JMenuItem("Empleados");
        itemEmpleados.addActionListener(e -> new EmpleadoFrame().setVisible(true));
        menuGestion.add(itemEmpleados);

        JMenuItem itemNomina = new JMenuItem("Generar Nómina");
        itemNomina.addActionListener(e -> new NominaFrame().setVisible(true));
        menuGestion.add(itemNomina);

        JMenuItem itemPlanilla = new JMenuItem("Resumen de Planilla (Patrono)");
        itemPlanilla.addActionListener(e -> new ReportePlanillaFrame().setVisible(true));
        menuGestion.add(itemPlanilla);

        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuSistema.add(itemSalir);

        barra.add(menuGestion);
        barra.add(menuSistema);
        return barra;
    }
}
