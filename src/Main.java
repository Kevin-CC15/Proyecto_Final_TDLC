import vista.MainView;
import controlador.MainController;

import javax.swing.SwingUtilities;

/**
 * Clase principal que arranca la aplicación de Simulación de Lenguajes Formales.
 * Inicia la interfaz de usuario (MainView) y el Controlador (MainController).
 * Este archivo debe estar en la carpeta 'src'.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Crear la Vista (la ventana principal)
                MainView view = new MainView();

                // 2. Crear el Controlador y pasarle la Vista
                MainController controller = new MainController(view);

                // 3. Conectar los eventos del Menú al Controlador
                view.getMenuPanel().setActionListener(controller);

                // 4. Hacer visible la ventana
                view.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación principal:");
                e.printStackTrace();
            }
        });
    }
}