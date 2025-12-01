package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Clase auxiliar para dibujar un ícono circular (como un estado de autómata)
 * con un color específico y un radio dado, para ser usado en componentes Swing.
 */
public class CircleIcon implements Icon {
    private final int radius;
    private final Color color;
    private final boolean isFinal;

    /**
     * @param radius El radio del círculo.
     * @param color El color de relleno.
     * @param isFinal Si es true, dibuja un círculo doble para indicar estado final.
     */
    public CircleIcon(int radius, Color color, boolean isFinal) {
        this.radius = radius;
        this.color = color;
        this.isFinal = isFinal;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = 2 * radius;

        // Dibuja el círculo de relleno
        g2d.setColor(color);
        g2d.fillOval(x, y, diameter, diameter);

        // Dibuja el borde
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(x, y, diameter, diameter);

        // Si es estado final, dibuja un círculo interior
        if (isFinal) {
            int innerRadius = radius - 4;
            int innerDiameter = 2 * innerRadius;
            int innerX = x + (diameter - innerDiameter) / 2;
            int innerY = y + (diameter - innerDiameter) / 2;
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(innerX, innerY, innerDiameter, innerDiameter);
        }

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return 2 * radius + 2; // Añade un poco de margen
    }

    @Override
    public int getIconHeight() {
        return 2 * radius + 2; // Añade un poco de margen
    }
}