package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import modelo.AutomataPila; // Se usa para la constante LAMBDA

/**
 * Panel de la interfaz gráfica para la definición y simulación del Autómata con Pila (AP).
 */
public class APPanel extends JPanel {
    public static final String CMD_SIMULAR_AP = "SIMULAR_AP";

    private final JTextField estadosInput = new JTextField();
    private final JTextField alfabetoInput = new JTextField();
    private final JTextField alfabetoPilaInput = new JTextField();
    private final JTextField estadoInicialInput = new JTextField();
    private final JTextField simboloInicialPilaInput = new JTextField();
    private final JTextField estadosFinalesInput = new JTextField();
    private final JTextArea transicionesInput = new JTextArea(8, 40);
    private final JTextField cadenaPruebaInput = new JTextField();
    private final JTextArea resultadoOutput = new JTextArea(10, 40);

    public APPanel(ActionListener listener) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Panel de Título y Botón
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Autómata con Pila (AP)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("<- Volver al Menú");
        backButton.setActionCommand(MainView.CARD_MENU);
        backButton.addActionListener(listener);
        headerPanel.add(backButton, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Panel Central de Definición
        JPanel definitionPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        definitionPanel.setBorder(BorderFactory.createTitledBorder("Definición Formal del AP"));

        definitionPanel.add(new JLabel("Q (Estados, ej: q0, q1):"));
        definitionPanel.add(estadosInput);

        definitionPanel.add(new JLabel("Σ (Alfabeto, ej: a, b):"));
        definitionPanel.add(alfabetoInput);

        definitionPanel.add(new JLabel("Γ (Alfabeto de la Pila, ej: A, Z):"));
        definitionPanel.add(alfabetoPilaInput);

        definitionPanel.add(new JLabel("q0 (Estado Inicial, ej: q0):"));
        definitionPanel.add(estadoInicialInput);

        // Etiqueta modificada para el nuevo requisito
        definitionPanel.add(new JLabel("Z0 (Símbolo Inicial de Pila, use 'λ' para Pila Vacía):"));
        definitionPanel.add(simboloInicialPilaInput);

        definitionPanel.add(new JLabel("F (Estados Finales, ej: qf):"));
        definitionPanel.add(estadosFinalesInput);

        // 3. Transiciones
        JPanel transicionesPanel = new JPanel(new BorderLayout());
        transicionesPanel.setBorder(BorderFactory.createTitledBorder(
                "Transiciones δ(q, a, X) -> (p, γ). Usar 'λ' para lambda. (ej: q0,a,Z=q0,AZ)"
        ));
        transicionesInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transicionesPanel.add(new JScrollPane(transicionesInput), BorderLayout.CENTER);

        // Contenedor de definición y transiciones
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(definitionPanel, BorderLayout.NORTH);
        centerPanel.add(transicionesPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 4. Panel de Simulación y Resultados
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));

        JPanel simulationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        simulationPanel.add(new JLabel("Cadena de Prueba:"));
        simulationPanel.add(cadenaPruebaInput);
        cadenaPruebaInput.setColumns(15);

        JButton btnSimular = new JButton("Simular Cadena (F & Pila Vacía)");
        btnSimular.setActionCommand(CMD_SIMULAR_AP);
        btnSimular.addActionListener(listener);
        simulationPanel.add(btnSimular);

        JButton btnCargar = new JButton("Cargar Ejemplo por Defecto");
        btnCargar.addActionListener(e -> cargarEjemploDefecto());
        simulationPanel.add(btnCargar);

        southPanel.add(simulationPanel, BorderLayout.NORTH);

        // Área de resultados
        resultadoOutput.setEditable(false);
        resultadoOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultsScroll = new JScrollPane(resultadoOutput);
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Resultado de la Simulación"));
        southPanel.add(resultsScroll, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);

        cargarEjemploDefecto(); // Cargar ejemplo al inicio
    }

    private void cargarEjemploDefecto() {
        // Ejemplo CLÁSICO: L = {a^n b^n | n >= 1}, con Pila Vacía Inicial
        estadosInput.setText("q0, q1, qf");
        alfabetoInput.setText("a, b");
        alfabetoPilaInput.setText("A");
        estadoInicialInput.setText("q0");
        estadosFinalesInput.setText("qf");
        simboloInicialPilaInput.setText(AutomataPila.LAMBDA); // CORRECCIÓN: Pila Vacía Inicial

        String ejemploTransiciones =
                // 1. Empujar 'A' por cada 'a'. La primera 'a' debe usar λ si la pila está vacía.
                "q0,a,λ=q0,A\n" +   // 1ra 'a': Pila vacía (λ), meter 'A'
                        "q0,a,A=q0,AA\n" +  // Siguientes 'a': Sacar 'A', meter 'AA'
                        // 2. Transición NO DETERMINISTA: cambiar a q1 sin consumir entrada
                        "q0,λ,A=q1,A\n" +   // Si hay un 'A' en el tope, pasa a desapilamiento (no saca A, mete A)
                        "q0,λ,λ=qf,λ\n" +   // Caso especial: L(λ). Pila vacía, pasa a final.
                        // 3. Desapilar 'A' por cada 'b'
                        "q1,b,A=q1,λ\n" +
                        // 4. Aceptación: Si llega a q1 con pila vacía (transición no leída en la entrada), pasa a qf
                        "q1,λ,λ=qf,λ\n";

        transicionesInput.setText(ejemploTransiciones);
        cadenaPruebaInput.setText("aaabbb");
        mostrarResultado("Ejemplo de AP (a^n b^n) cargado. Acepta por F y Pila Vacía (Z0=λ).\nNota: La transición q0,a,λ=q0,A es clave para el inicio con pila vacía.");
    }

    // --- Métodos Getters para el Controlador (sin cambios) ---
    public String getEstadosInput() { return estadosInput.getText().trim(); }
    public String getAlfabetoInput() { return alfabetoInput.getText().trim(); }
    public String getAlfabetoPilaInput() { return alfabetoPilaInput.getText().trim(); }
    public String getEstadoInicialInput() { return estadoInicialInput.getText().trim(); }
    public String getSimboloInicialPilaInput() { return simboloInicialPilaInput.getText().trim(); }
    public String getEstadosFinalesInput() { return estadosFinalesInput.getText().trim(); }
    public String getTransicionesInput() { return transicionesInput.getText().trim(); }
    public String getCadenaPruebaInput() { return cadenaPruebaInput.getText().trim(); }

    public void mostrarResultado(String resultado) {
        resultadoOutput.setText(resultado);
        resultadoOutput.setCaretPosition(0);
    }
}