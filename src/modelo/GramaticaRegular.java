package modelo;

import java.util.Map;
import java.util.Set;

/**
 * Representa una Gramática Regular (GR).
 * La funcionalidad principal es verificar si una cadena pertenece al lenguaje.
 * También incluirá el método para la transformación a AFD (opcional, pero buena práctica).
 */
public class GramaticaRegular {

    // V: Conjunto de variables (no terminales)
    private final Set<String> variables;

    // Σ: Conjunto de terminales
    private final Set<String> terminales;

    // P: Conjunto de producciones. Map<Variable, Set<CuerposDeProduccion>>
    private final Map<String, Set<String>> producciones;

    // S: Símbolo inicial
    private final String simboloInicial;

    /**
     * Constructor de la Gramática Regular.
     */
    public GramaticaRegular(Set<String> variables, Set<String> terminales,
                            Map<String, Set<String>> producciones, String simboloInicial) {
        this.variables = variables;
        this.terminales = terminales;
        this.producciones = producciones;
        this.simboloInicial = simboloInicial;
    }

    /**
     * Verifica si una cadena puede ser generada por la gramática (pertenece al lenguaje).
     * Nota: Para una GR, la forma más sencilla de verificar es simular el AFD equivalente.
     * Aquí simularemos una verificación recursiva simplificada o se usará el AFD equivalente.
     * * @param cadena La cadena a verificar.
     * @param logBuffer Buffer para registrar el proceso (si se implementa como simulación).
     * @return true si la cadena es válida, false en caso contrario.
     */
    public boolean verificarCadena(String cadena, StringBuilder logBuffer) {
        logBuffer.append("--- INICIO DE VERIFICACIÓN (GR) ---\n");
        logBuffer.append("Cadena a evaluar: ").append(cadena).append("\n");

        logBuffer.append("Implementación completa pendiente: Se requiere transformación a AFD/AFN.\n");
        logBuffer.append("Por ahora, la verificación es un STUB y aceptará cadenas que solo usen terminales simples.\n");

        if (cadena.isEmpty()) {
            return false; // Una GR pura no acepta lambda a menos que haya S -> lambda.
        }

        boolean valida = verificarRecursivo(simboloInicial, cadena, logBuffer, 0);

        logBuffer.append("RESULTADO: ").append(valida ? "ACEPTADA" : "RECHAZADA").append("\n");
        logBuffer.append("---------------------------------\n");
        return valida;
    }

    /**
     * Lógica recursiva simple para la simulación de la derivación.
     * Esto es una simulación MUY básica y no cubre todos los casos de una GR/AFN.
     */
    private boolean verificarRecursivo(String variableActual, String subcadena, StringBuilder logBuffer, int nivel) {
        String padding = "  ".repeat(nivel);

        if (subcadena.isEmpty()) {
            // Si la subcadena está vacía, solo es aceptable si la variable actual tiene una producción a λ (epsilon).
            if (producciones.getOrDefault(variableActual, Set.of()).contains("λ") || producciones.getOrDefault(variableActual, Set.of()).contains("ε")) {
                logBuffer.append(padding).append("-> ").append(variableActual).append(" -> λ (Éxito parcial)\n");
                return true;
            }
            return false;
        }

        Set<String> reglas = producciones.getOrDefault(variableActual, Set.of());

        for (String regla : reglas) {
            // Regla es terminal
            if (terminales.contains(regla)) {
                if (regla.equals(subcadena)) {
                    logBuffer.append(padding).append("-> ").append(variableActual).append(" -> ").append(regla).append(" (Cadena consumida)\n");
                    return true;
                }
            }
            // Regla es terminal + variable (ej: aB)
            else if (regla.length() == 2 && terminales.contains(String.valueOf(regla.charAt(0))) && variables.contains(String.valueOf(regla.charAt(1)))) {
                String terminal = String.valueOf(regla.charAt(0));
                String siguienteVariable = String.valueOf(regla.charAt(1));

                if (subcadena.startsWith(terminal)) {
                    String restante = subcadena.substring(1);
                    logBuffer.append(padding).append("-> ").append(variableActual).append(" -> ").append(terminal).append(siguienteVariable)
                            .append(". Restante: ").append(restante).append("\n");

                    if (verificarRecursivo(siguienteVariable, restante, logBuffer, nivel + 1)) {
                        return true;
                    }
                }
            }
            // Regla es solo terminal (ej: a)
            else if (regla.length() == 1 && terminales.contains(regla)) {
                if (regla.equals(subcadena)) {
                    logBuffer.append(padding).append("-> ").append(variableActual).append(" -> ").append(regla).append(" (Cadena consumida)\n");
                    return true;
                }
            }
        }

        return false;
    }
}