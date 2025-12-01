package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel para que el usuario ingrese la Gramática Regular (GR).
 */
public class GRPanel extends JPanel {

    public static final String CMD_VERIFICAR_GR = "VERIFICAR_GR";

    private JTextField txtVariables;
    private JTextField txtTerminales;
    private JTextField txtSimboloInicial;
    private JTextArea txtProducciones;
    private JTextField txtCadenaPrueba;
    private JTextArea txtResultados;

    public GRPanel(ActionListener listener) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Título y Botón Volver
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(getBackground());
        JLabel title = new JLabel("Definición y Verificación de Gramática Regular (GR)");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JButton btnVolver = new JButton("<- Volver al Menú");
        btnVolver.setActionCommand(MainView.CARD_MENU);
        btnVolver.addActionListener(listener);
        header.add(btnVolver, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. Contenedor de Formulario y Resultados (Dividido)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);

        splitPane.setLeftComponent(createDefinicionPanel(listener));
        splitPane.setRightComponent(createResultadosPanel());

        add(splitPane, BorderLayout.CENTER);

        cargarEjemploDefecto();
    }

    private JPanel createDefinicionPanel(ActionListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)), "Definición Formal (V, Σ, P, S)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(20, 60, 100)));
        panel.setBackground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.BOLD, 12);

        // --- Variables (V) ---
        txtVariables = new JTextField(20);
        txtVariables.setToolTipText("Ej: S, A, B");
        panel.add(createLabeledField("Variables (V / No Terminales):", txtVariables, labelFont));

        // --- Terminales (Σ) ---
        txtTerminales = new JTextField(20);
        txtTerminales.setToolTipText("Ej: a, b");
        panel.add(createLabeledField("Terminales (Σ):", txtTerminales, labelFont));

        // --- Símbolo Inicial (S) ---
        txtSimboloInicial = new JTextField(20);
        txtSimboloInicial.setToolTipText("Ej: S");
        panel.add(createLabeledField("Símbolo Inicial (S):", txtSimboloInicial, labelFont));

        // --- Producciones (P) ---
        JLabel lblProducciones = new JLabel("Producciones (P): Formato: A -> aB | b | c (una por línea, use '|' para alternativas, 'λ' para epsilon)");
        lblProducciones.setFont(labelFont);
        panel.add(lblProducciones);
        txtProducciones = new JTextArea(7, 20);
        JScrollPane scrollProducciones = new JScrollPane(txtProducciones);
        scrollProducciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        panel.add(scrollProducciones);

        panel.add(Box.createVerticalStrut(15));

        // --- Cadena de Prueba ---
        txtCadenaPrueba = new JTextField(20);
        panel.add(createLabeledField("Cadena a Probar:", txtCadenaPrueba, new Font("Arial", Font.BOLD, 14)));

        // --- Botón de Verificación ---
        JButton btnVerificar = new JButton("VERIFICAR CADENA");
        btnVerificar.setActionCommand(CMD_VERIFICAR_GR);
        btnVerificar.addActionListener(listener);
        btnVerificar.setBackground(new Color(52, 152, 219)); // Azul
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVerificar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnVerificar);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

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

    private JPanel createResultadosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)), "Resultados y Proceso de Derivación",
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

    private void cargarEjemploDefecto() {
        txtVariables.setText("S, A");
        txtTerminales.setText("a, b");
        txtSimboloInicial.setText("S");
        txtProducciones.setText(
                "S -> aA | b\n" +
                        "A -> bS | a"
        );
        txtCadenaPrueba.setText("ababa");
        mostrarResultado("Ejemplo de Gramática Regular cargado.\nPresione VERIFICAR CADENA para iniciar.");
    }

    // --- Métodos de acceso para el Controlador ---

    public String getVariablesInput() { return txtVariables.getText().trim(); }
    public String getTerminalesInput() { return txtTerminales.getText().trim(); }
    public String getSimboloInicialInput() { return txtSimboloInicial.getText().trim(); }
    public String getProduccionesInput() { return txtProducciones.getText().trim(); }
    public String getCadenaPruebaInput() { return txtCadenaPrueba.getText().trim(); }

    public void mostrarResultado(String resultado) {
        txtResultados.setText(resultado);
        txtResultados.setCaretPosition(0);
    }

    public void agregarLineaResultado(String linea) {
        txtResultados.append(linea + "\n");
        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
    }
}