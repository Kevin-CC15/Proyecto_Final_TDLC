package controlador;

import modelo.AFD;
import modelo.GramaticaRegular;
import modelo.GramaticaLibreContexto;
import modelo.AutomataPila;
import vista.AFDPanel;
import vista.GRPanel;
import vista.GLCPanel;
import vista.APPanel;
import vista.MainView;
import vista.MenuPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador principal que maneja los eventos de la aplicación y coordina
 * entre la Vista (GUI) y el Modelo (AFD, GR, GLC, AP, etc.).
 */
public class MainController implements ActionListener {
    private MainView view;
    private AFDPanel afdPanel;
    private GRPanel grPanel;
    private GLCPanel glcPanel;
    private APPanel apPanel;

    public MainController(MainView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            // Comandos de navegación
            case MenuPanel.CMD_AFD:
                if (afdPanel == null) {
                    afdPanel = new AFDPanel(this);
                    view.addView(afdPanel, MainView.CARD_AFD);
                }
                view.showCard(MainView.CARD_AFD);
                break;
            case MenuPanel.CMD_GR:
                if (grPanel == null) {
                    grPanel = new GRPanel(this);
                    view.addView(grPanel, MainView.CARD_GR);
                }
                view.showCard(MainView.CARD_GR);
                break;
            case MenuPanel.CMD_GLC:
                if (glcPanel == null) {
                    glcPanel = new GLCPanel(this);
                    view.addView(glcPanel, MainView.CARD_GLC);
                }
                view.showCard(MainView.CARD_GLC);
                break;
            case MenuPanel.CMD_AP:
                if (apPanel == null) {
                    apPanel = new APPanel(this);
                    view.addView(apPanel, MainView.CARD_AP);
                }
                view.showCard(MainView.CARD_AP);
                break;
            case MainView.CARD_MENU:
                view.showCard(MainView.CARD_MENU);
                break;

            // Comandos de simulación y verificación
            case AFDPanel.CMD_SIMULAR_AFD:
                simularAFD();
                break;
            case GRPanel.CMD_VERIFICAR_GR:
                verificarGR();
                break;
            case GLCPanel.CMD_VERIFICAR_GLC:
                verificarGLC();
                break;
            case APPanel.CMD_SIMULAR_AP:
                simularAP();
                break;

            default:
                // Manejo de comandos desconocidos
                System.out.println("Comando desconocido: " + command);
        }
    }

    //  LÓGICA DE SIMULACIÓN Y VERIFICACIÓN

    private void simularAFD() {
        try {
            // 1. Obtener datos de la Vista
            Set<String> estados = parseList(afdPanel.getEstadosInput());
            Set<String> alfabeto = parseList(afdPanel.getAlfabetoInput());
            String estadoInicial = afdPanel.getEstadoInicialInput();
            Set<String> estadosFinales = parseList(afdPanel.getEstadosFinalesInput());
            String transicionesRaw = afdPanel.getTransicionesInput();
            String cadenaPrueba = afdPanel.getCadenaPruebaInput();

            if (estadoInicial.isEmpty()) {
                throw new IllegalArgumentException("El Estado Inicial es obligatorio.");
            }
            if (estadosFinales.isEmpty()) {
                throw new IllegalArgumentException("El conjunto de Estados Finales (F) no puede estar vacío.");
            }

            // 2. Parsear transiciones y crear el Modelo
            Map<String, Map<String, String>> delta = parseRawTransicionesAFD(transicionesRaw, estados, alfabeto);

            AFD afd = new AFD(estados, alfabeto, delta, estadoInicial, estadosFinales);

            // 3. Simular y 4. Mostrar resultado en la Vista
            StringBuilder logBuffer = new StringBuilder();
            afd.simularCadena(cadenaPrueba, logBuffer);

            afdPanel.mostrarResultado(logBuffer.toString());

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Error de definición en el AFD: " + e.getMessage(), "Error de AFD", JOptionPane.ERROR_MESSAGE);
            afdPanel.mostrarResultado("ERROR: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error desconocido al procesar el AFD: " + e.getMessage(), "Error de AFD", JOptionPane.ERROR_MESSAGE);
            afdPanel.mostrarResultado("ERROR: " + e.getMessage());
        }
    }

    private void verificarGR() {
        try {
            // 1. Obtener datos de la Vista
            Set<String> variables = parseList(grPanel.getVariablesInput());
            Set<String> terminales = parseList(grPanel.getTerminalesInput());
            String simboloInicial = grPanel.getSimboloInicialInput();
            String produccionesRaw = grPanel.getProduccionesInput();
            String cadenaPrueba = grPanel.getCadenaPruebaInput();

            if (simboloInicial.isEmpty()) {
                throw new IllegalArgumentException("El Símbolo Inicial (S) es obligatorio.");
            }
            if (!variables.contains(simboloInicial)) {
                throw new IllegalArgumentException("El Símbolo Inicial debe estar en el conjunto de Variables.");
            }

            // 2. Parsear producciones y crear el Modelo
            Map<String, Set<String>> producciones = parseRawProducciones(produccionesRaw);

            // TODO: Agregar validación para el formato Regular de las producciones si es necesario.

            GramaticaRegular gr = new GramaticaRegular(variables, terminales, producciones, simboloInicial);

            // 3. Verificar y 4. Mostrar resultado en la Vista
            StringBuilder logBuffer = new StringBuilder();
            boolean aceptada = gr.verificarCadena(cadenaPrueba, logBuffer);

            String resultadoFinal = logBuffer.toString();
            grPanel.mostrarResultado(resultadoFinal);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Error de definición en la GR: " + e.getMessage(), "Error de GR", JOptionPane.ERROR_MESSAGE);
            grPanel.mostrarResultado("ERROR: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error desconocido al procesar la GR: " + e.getMessage(), "Error de GR", JOptionPane.ERROR_MESSAGE);
            grPanel.mostrarResultado("ERROR: " + e.getMessage());
        }
    }

    private void verificarGLC() {
        try {
            // 1. Obtener datos de la Vista
            Set<String> variables = parseList(glcPanel.getVariablesInput());
            Set<String> terminales = parseList(glcPanel.getTerminalesInput());
            String simboloInicial = glcPanel.getSimboloInicialInput();
            String produccionesRaw = glcPanel.getProduccionesInput();
            String cadenaPrueba = glcPanel.getCadenaPruebaInput();

            if (simboloInicial.isEmpty()) {
                throw new IllegalArgumentException("El Símbolo Inicial (S) es obligatorio.");
            }
            if (!variables.contains(simboloInicial)) {
                throw new IllegalArgumentException("El Símbolo Inicial debe estar en el conjunto de Variables.");
            }

            // 2. Parsear producciones y crear el Modelo
            Map<String, Set<String>> producciones = parseRawProducciones(produccionesRaw);

            GramaticaLibreContexto glc = new GramaticaLibreContexto(variables, terminales, producciones, simboloInicial);

            // 3. Verificar y 4. Mostrar resultado en la Vista
            StringBuilder logBuffer = new StringBuilder();
            boolean aceptada = glc.verificarCadena(cadenaPrueba, logBuffer);

            String resultadoFinal = logBuffer.toString();
            glcPanel.mostrarResultado(resultadoFinal);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Error de definición en la GLC: " + e.getMessage(), "Error de GLC", JOptionPane.ERROR_MESSAGE);
            glcPanel.mostrarResultado("ERROR: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error desconocido al procesar la GLC: " + e.getMessage(), "Error de GLC", JOptionPane.ERROR_MESSAGE);
            glcPanel.mostrarResultado("ERROR: " + e.getMessage());
        }
    }


    /**
     * Lógica de simulación del Autómata con Pila (AP).
     * CORRECCIÓN: Lee el Símbolo Inicial de Pila (Z0) para permitir "λ" (pila vacía).
     */
    private void simularAP() {
        try {
            // 1. Obtener datos de la Vista
            Set<String> estados = parseList(apPanel.getEstadosInput());
            Set<String> alfabeto = parseList(apPanel.getAlfabetoInput());
            Set<String> alfabetoPila = parseList(apPanel.getAlfabetoPilaInput());
            String estadoInicial = apPanel.getEstadoInicialInput().trim();

            // Simbolo Inicial de Pila. Se pasa directamente a AutomataPila, que lo validará (puede ser "λ" para pila vacía).
            String simboloInicialPila = apPanel.getSimboloInicialPilaInput().trim();

            Set<String> estadosFinales = parseList(apPanel.getEstadosFinalesInput());
            String transicionesRaw = apPanel.getTransicionesInput();
            String cadenaPrueba = apPanel.getCadenaPruebaInput();

            if (estadoInicial.isEmpty()) {
                throw new IllegalArgumentException("El Estado Inicial es obligatorio.");
            }
            if (estadosFinales.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Advertencia: Para la aceptación solicitada ('Final y Pila Vacía'), el conjunto de Estados Finales (F) debe estar definido y la simulación lo usará.", "Advertencia de AP", JOptionPane.WARNING_MESSAGE);
            }

            // 2. Parsear transiciones y crear el Modelo
            // Utilizamos el método estático de la clase AutomataPila para el parseo complejo de transiciones
            Map<String, Set<AutomataPila.TransicionAP>> delta = AutomataPila.parseRawTransiciones(transicionesRaw, estados, alfabeto, alfabetoPila);

            AutomataPila ap = new AutomataPila(estados, alfabeto, alfabetoPila, delta, estadoInicial, simboloInicialPila, estadosFinales);

            // 3. Simular y 4. Mostrar resultado en la Vista
            StringBuilder logBuffer = new StringBuilder();
            boolean aceptada = ap.simularCadena(cadenaPrueba, logBuffer);

            String resultadoFinal = logBuffer.toString();
            apPanel.mostrarResultado(resultadoFinal);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Error de definición en el AP: " + e.getMessage(), "Error de AP", JOptionPane.ERROR_MESSAGE);
            apPanel.mostrarResultado("ERROR: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error desconocido al procesar el AP: " + e.getMessage(), "Error de AP", JOptionPane.ERROR_MESSAGE);
            apPanel.mostrarResultado("ERROR: " + e.getMessage());
        }
    }

    //  MÉTODOS AUXILIARES DE PARSEO
    /**
     * Parsea una cadena de texto separada por comas (o espacios) en un Set<String>.
     * Por ejemplo: "q0, q1, qf" -> {"q0", "q1", "qf"}
     */
    private Set<String> parseList(String rawList) {
        if (rawList == null || rawList.trim().isEmpty()) {
            return new HashSet<>();
        }
        // Dividir por coma, espacio, o nueva línea.
        return Arrays.stream(rawList.split("[,\\s\\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Parsea la cadena de texto de transiciones a la estructura de datos para AFD.
     * Formato esperado: q,a=p (ejemplo: q0,0=q1)
     */
    private Map<String, Map<String, String>> parseRawTransicionesAFD(String rawTransiciones, Set<String> estados, Set<String> alfabeto) {
        Map<String, Map<String, String>> delta = new HashMap<>();
        String[] lineas = rawTransiciones.split("\\n");

        for (String line : lineas) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;

            try {
                // 1. Separar: "q,a" y "p"
                String[] partes = trimmedLine.split("=");
                if (partes.length != 2) throw new IllegalArgumentException("El formato debe ser 'q,a=p'.");

                // 2. Parsear el lado izquierdo: q,a
                String[] izq = partes[0].split(",");
                if (izq.length != 2) throw new IllegalArgumentException("El lado izquierdo ('q,a') debe tener 2 componentes separados por coma.");
                String q = izq[0].trim();
                String a = izq[1].trim();

                // 3. Parsear el lado derecho: p
                String p = partes[1].trim();

                // Validaciones de consistencia
                if (!estados.contains(q) || !estados.contains(p)) {
                    throw new IllegalArgumentException("Estado '" + q + "' o '" + p + "' no está en el conjunto de estados Q.");
                }
                if (!alfabeto.contains(a)) {
                    throw new IllegalArgumentException("Símbolo de entrada '" + a + "' no está en el alfabeto Σ.");
                }

                // 4. Crear y añadir la transición
                delta.putIfAbsent(q, new HashMap<>());
                if (delta.get(q).containsKey(a)) {
                    throw new IllegalArgumentException("Transición duplicada para el par (" + q + ", " + a + ") en un AFD.");
                }
                delta.get(q).put(a, p);

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en la transición: '" + trimmedLine + "'. " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException("Error de sintaxis desconocido en la transición: '" + trimmedLine + "'.");
            }
        }
        return delta;
    }

    /**
     * Parsea la cadena de texto de producciones a la estructura de datos para Gramáticas.
     * Formato esperado: V -> cuerpo1 | cuerpo2 (ejemplo: S -> aA | b)
     */
    private Map<String, Set<String>> parseRawProducciones(String rawProducciones) {
        Map<String, Set<String>> producciones = new HashMap<>();
        String[] lineas = rawProducciones.split("\\n");

        for (String line : lineas) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;

            try {
                // 1. Separar: "V" y "cuerpo1 | cuerpo2"
                // Ejemplo: "S -> aA | b" -> ["S", "aA | b"]
                String[] partes = trimmedLine.split("->");
                if (partes.length != 2) throw new IllegalArgumentException("Debe contener un '->'.");

                String variable = partes[0].trim();
                String cuerposStr = partes[1].trim();

                if (variable.isEmpty() || cuerposStr.isEmpty()) {
                    throw new IllegalArgumentException("La variable o los cuerpos de producción están vacíos.");
                }

                // Separar Cuerpos: "aA | b" -> ["aA", "b"]
                Set<String> cuerpos = Arrays.stream(cuerposStr.split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());

                if (cuerpos.isEmpty()) {
                    throw new IllegalArgumentException("No se encontraron cuerpos de producción después de '->'.");
                }

                producciones.putIfAbsent(variable, new HashSet<>());
                producciones.get(variable).addAll(cuerpos);

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en la producción: '" + trimmedLine + "'. " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException("Error de sintaxis desconocido en la producción: '" + trimmedLine + "'.");
            }
        }

        return producciones;
    }
}