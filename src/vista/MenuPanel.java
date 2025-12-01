package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel que muestra el menú principal para seleccionar el tipo de autómata/gramática.
 */
public class MenuPanel extends JPanel {

    // Comandos de acción que se envían al controlador
    public static final String CMD_AFD = "SHOW_AFD";
    public static final String CMD_GR = "SHOW_GR";
    public static final String CMD_GLC = "SHOW_GLC";
    public static final String CMD_AP = "SHOW_AP";

    private JButton btnAFD;
    private JButton btnGR;
    private JButton btnGLC;
    private JButton btnAP;

    public MenuPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(230, 240, 250)); // Fondo claro y agradable

        // Título
        JLabel title = new JLabel("Herramienta de Simulación de Lenguajes Formales", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(30, 60, 90));
        add(title, BorderLayout.NORTH);

        // Panel de botones (Grid)
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        buttonsPanel.setBackground(getBackground());

        btnAFD = createStyledButton("Autómata Finito Determinista (AFD)", CMD_AFD, new Color(52, 152, 219)); // Azul
        btnGR = createStyledButton("Gramática Regular (GR)", CMD_GR, new Color(46, 204, 113)); // Verde
        btnGLC = createStyledButton("Gramática Libre de Contexto (GLC)", CMD_GLC, new Color(241, 196, 15)); // Amarillo/Naranja
        btnAP = createStyledButton("Autómata de Pila (AP)", CMD_AP, new Color(155, 89, 182)); // Púrpura

        buttonsPanel.add(btnAFD);
        buttonsPanel.add(btnGR);
        buttonsPanel.add(btnGLC);
        buttonsPanel.add(btnAP);

        add(buttonsPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Proyecto Teoría de Lenguajes y Compiladores", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 14));
        footer.setForeground(new Color(100, 100, 100));
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Helper para crear botones con estilo.
     */
    private JButton createStyledButton(String text, String command, Color color) {
        JButton button = new JButton(text);
        button.setActionCommand(command);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover simple
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    /**
     * Asigna el ActionListener (Controlador) a todos los botones.
     */
    public void setActionListener(ActionListener listener) {
        btnAFD.addActionListener(listener);
        btnGR.addActionListener(listener);
        btnGLC.addActionListener(listener);
        btnAP.addActionListener(listener);
    }
}