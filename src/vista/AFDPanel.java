package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel para que el usuario ingrese la definición formal de un Autómata Finito Determinista (AFD)
 * y la cadena a simular.
 */
public class AFDPanel extends JPanel {

    // Constante de comando para el controlador
    public static final String CMD_SIMULAR_AFD = "SIMULAR_AFD";

    // Campos de entrada
    private JTextField txtEstados;
    private JTextField txtAlfabeto;
    private JTextField txtEstadoInicial;
    private JTextField txtEstadosFinales;
    private JTextArea txtTransiciones;
    private JTextField txtCadenaPrueba;

    // Área de resultados
    private JTextArea txtResultados;

    /**
     * Constructor del panel.
     * @param listener El controlador que recibirá los eventos.
     */
    public AFDPanel(ActionListener listener) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Título y Botón Volver
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(getBackground());
        JLabel title = new JLabel("Definición y Simulación de Autómata Finito Determinista (AFD)");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JButton btnVolver = new JButton("<- Volver al Menú");
        btnVolver.setActionCommand(MainView.CARD_MENU); // Usa el comando de navegación
        btnVolver.addActionListener(listener);
        header.add(btnVolver, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. Contenedor de Formulario y Resultados (Dividido)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450); // Divide el espacio
        splitPane.setResizeWeight(0.5);

        // 2.1 Panel de Definición Formal
        JPanel definicionPanel = createDefinicionPanel(listener);
        splitPane.setLeftComponent(definicionPanel);

        // 2.2 Panel de Resultados
        JPanel resultadosPanel = createResultadosPanel();
        splitPane.setRightComponent(resultadosPanel);

        add(splitPane, BorderLayout.CENTER);

        // Cargar un ejemplo por defecto para pruebas rápidas
        cargarEjemploDefecto();
    }

    /**
     * Crea el panel para la entrada de la 5-tupla y la cadena.
     */
    private JPanel createDefinicionPanel(ActionListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)), "Definición Formal (Q, Σ, δ, q0, F)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(20, 60, 100)));
        panel.setBackground(Color.WHITE);

        // Estilo de los JLabels
        Font labelFont = new Font("Arial", Font.BOLD, 12);

        // --- Estados (Q) ---
        txtEstados = new JTextField(20);
        txtEstados.setToolTipText("Ej: q0, q1, q2");
        panel.add(createLabeledField("Estados (Q):", txtEstados, labelFont));

        // --- Alfabeto (Σ) ---
        txtAlfabeto = new JTextField(20);
        txtAlfabeto.setToolTipText("Ej: 0, 1");
        panel.add(createLabeledField("Alfabeto (Σ):", txtAlfabeto, labelFont));

        // --- Estado Inicial (q0) ---
        txtEstadoInicial = new JTextField(20);
        txtEstadoInicial.setToolTipText("Ej: q0");
        panel.add(createLabeledField("Estado Inicial (q0):", txtEstadoInicial, labelFont));

        // --- Estados Finales (F) ---
        txtEstadosFinales = new JTextField(20);
        txtEstadosFinales.setToolTipText("Ej: q2");
        panel.add(createLabeledField("Estados Finales (F):", txtEstadosFinales, labelFont));

        // --- Transiciones (δ) ---
        JLabel lblTransiciones = new JLabel("Transiciones (δ): Formato: q0,0=q1 (una por línea)");
        lblTransiciones.setFont(labelFont);
        panel.add(lblTransiciones);
        txtTransiciones = new JTextArea(5, 20);
        JScrollPane scrollTransiciones = new JScrollPane(txtTransiciones);
        scrollTransiciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.add(scrollTransiciones);

        panel.add(Box.createVerticalStrut(15)); // Espacio separador

        // --- Cadena de Prueba ---
        txtCadenaPrueba = new JTextField(20);
        panel.add(createLabeledField("Cadena a Probar:", txtCadenaPrueba, new Font("Arial", Font.BOLD, 14)));

        // --- Botón de Simulación ---
        JButton btnSimular = new JButton("SIMULAR CADENA");
        btnSimular.setActionCommand(CMD_SIMULAR_AFD);
        btnSimular.addActionListener(listener);
        btnSimular.setBackground(new Color(39, 174, 96)); // Verde esmeralda
        btnSimular.setForeground(Color.WHITE);
        btnSimular.setFont(new Font("Arial", Font.BOLD, 14));
        btnSimular.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnSimular);

        panel.add(Box.createVerticalGlue()); // Empuja el contenido hacia arriba

        return panel;
    }

    /**
     * Crea un pequeño panel con etiqueta y campo de texto para el formulario.
     */
    private JPanel createLabeledField(String labelText, JComponent component, Font font) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 10));
        return panel;
    }

    /**
     * Crea el panel para mostrar el proceso de simulación y el resultado.
     */
    private JPanel createResultadosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)), "Resultados y Proceso de Simulación",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(20, 60, 100)));
        panel.setBackground(Color.WHITE);

        txtResultados = new JTextArea();
        txtResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtResultados.setEditable(false);
        txtResultados.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(txtResultados);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Carga un ejemplo simple de AFD que acepta cadenas con un número par de 0s.
     */
    private void cargarEjemploDefecto() {
        txtEstados.setText("q0, q1");
        txtAlfabeto.setText("0, 1");
        txtEstadoInicial.setText("q0");
        txtEstadosFinales.setText("q0");
        txtTransiciones.setText(
                "q0,0=q1\n" +
                        "q0,1=q0\n" +
                        "q1,0=q0\n" +
                        "q1,1=q1"
        );
        txtCadenaPrueba.setText("10010");
        mostrarResultado("Ejemplo de AFD (Paridad de 0s) cargado.\nPresione SIMULAR CADENA para iniciar.");
    }


    // --- Métodos de acceso para el Controlador ---

    public String getEstadosInput() { return txtEstados.getText().trim(); }
    public String getAlfabetoInput() { return txtAlfabeto.getText().trim(); }
    public String getEstadoInicialInput() { return txtEstadoInicial.getText().trim(); }
    public String getEstadosFinalesInput() { return txtEstadosFinales.getText().trim(); }
    public String getTransicionesInput() { return txtTransiciones.getText().trim(); }
    public String getCadenaPruebaInput() { return txtCadenaPrueba.getText().trim(); }

    public void mostrarResultado(String resultado) {
        txtResultados.setText(resultado);
        txtResultados.setCaretPosition(0); // Scroll al inicio
    }

    public void agregarLineaResultado(String linea) {
        txtResultados.append(linea + "\n");
        txtResultados.setCaretPosition(txtResultados.getDocument().getLength()); // Scroll al final
    }
}