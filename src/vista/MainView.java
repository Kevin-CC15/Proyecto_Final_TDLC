package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Contenedor principal de la aplicación.
 * Utiliza CardLayout para cambiar entre el menú y los paneles de simulación.
 */
public class MainView extends JFrame {

    // Constantes para el CardLayout
    public static final String CARD_MENU = "CardMenu";
    public static final String CARD_AFD = "CardAFD";
    public static final String CARD_GR = "CardGR";
    public static final String CARD_GLC = "CardGLC";
    public static final String CARD_AP = "CardAP";

    private JPanel cardPanel;
    private MenuPanel menuPanel;

    public MainView() {
        setTitle("Simulador de Lenguajes Formales - TDLC");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en la pantalla

        cardPanel = new JPanel(new CardLayout());

        // 1. Inicializar el menú y agregarlo como la primera "tarjeta"
        menuPanel = new MenuPanel();
        cardPanel.add(menuPanel, CARD_MENU);

        // 2. Agregar el panel principal al frame
        add(cardPanel);

        // Mostrar el menú al inicio
        showCard(CARD_MENU);
    }

    /**
     * Muestra la tarjeta (panel) solicitada por su nombre.
     */
    public void showCard(String cardName) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, cardName);
    }

    /**
     * Agrega un nuevo panel al CardLayout.
     */
    public void addView(JPanel panel, String cardName) {
        cardPanel.add(panel, cardName);
    }

    /**
     * Método necesario para que el Main.java acceda al MenuPanel y asigne el controlador.
     */
    public MenuPanel getMenuPanel() {
        return menuPanel;
    }
}