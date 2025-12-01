package modelo;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Stack;

/**
 * Representa una Gramática Libre de Contexto (GLC).
 * Utiliza el algoritmo de análisis sintáctico descendente (Top-Down) o CYK/LL/LR
 * para verificar si una cadena pertenece al lenguaje. Aquí usaremos un parser simple.
 */
public class GramaticaLibreContexto {

    // V: Conjunto de variables (no terminales)
    private final Set<String> variables;

    // Σ: Conjunto de terminales
    private final Set<String> terminales;

    // P: Conjunto de producciones. Map<Variable, Set<CuerposDeProduccion>>
    private final Map<String, Set<String>> producciones;

    // S: Símbolo inicial
    private final String simboloInicial;

    /**
     * Constructor de la GLC.
     */
    public GramaticaLibreContexto(Set<String> variables, Set<String> terminales,
                                  Map<String, Set<String>> producciones, String simboloInicial) {
        this.variables = variables;
        this.terminales = terminales;
        this.producciones = producciones;
        this.simboloInicial = simboloInicial;
    }

    /**
     * Verifica si una cadena puede ser generada por la gramática (pertenece al lenguaje)
     * utilizando un analizador sintáctico descendente (Top-Down) no determinista simple.
     * * NOTA: Esta implementación es un STUB muy básico y NO ES un parser LL(1) o LALR(1)
     * completo. Solo demuestra la estructura. Una GLC real requeriría un algoritmo
     * como CYK, Earley, o una tabla de parsing LL/LR.
     * * @param cadena La cadena a verificar.
     * @param logBuffer Buffer para registrar el proceso.
     * @return true si la cadena es válida, false en caso contrario.
     */
    public boolean verificarCadena(String cadena, StringBuilder logBuffer) {
        logBuffer.append("--- INICIO DE VERIFICACIÓN (GLC) ---\n");
        logBuffer.append("Cadena a evaluar: ").append(cadena).append("\n");

        // Usamos una pila para simular el proceso de derivación (Autómata con Pila implícito)
        Stack<String> pila = new Stack<>();
        pila.push("#"); // Símbolo de fondo de pila
        pila.push(simboloInicial);

        String input = cadena + "#"; // Símbolo de fin de entrada
        int indiceEntrada = 0;
        int paso = 1;

        logBuffer.append("Pila Inicial: [#, ").append(simboloInicial).append("], Entrada: ").append(input).append("\n");
        logBuffer.append("==================================\n");

        // Implementación básica de análisis sintáctico descendente (backtracking/fuerza bruta)
        boolean aceptada = backtrackParse(simboloInicial, cadena, logBuffer, 0, new Stack<>(), 0);

        logBuffer.append("==================================\n");
        logBuffer.append("RESULTADO: ").append(aceptada ? "ACEPTADA" : "RECHAZADA").append("\n");
        logBuffer.append("---------------------------------\n");
        return aceptada;
    }

    /**
     * Función recursiva de backtracking para el parsing de GLC.
     * Demasiado lenta para GLCs ambiguas o grandes, pero sirve para la demostración.
     */
    private boolean backtrackParse(String variableActual, String subcadena, StringBuilder logBuffer, int nivel, Stack<String> pilaSimulada, int paso) {
        String padding = "  ".repeat(nivel);

        // Caso base de éxito: Cadena consumida y variable resuelta (o producción a λ)
        if (subcadena.isEmpty() && (producciones.getOrDefault(variableActual, Set.of()).contains("λ") || producciones.getOrDefault(variableActual, Set.of()).contains("ε"))) {
            logBuffer.append(padding).append("Paso ").append(paso++).append(": ").append(variableActual).append(" -> λ (Fin de cadena)\n");
            return true;
        }

        Set<String> reglas = producciones.getOrDefault(variableActual, Set.of());

        for (String regla : reglas) {

            // 1. Regla terminal (ej: a)
            if (regla.length() == 1 && terminales.contains(regla)) {
                if (subcadena.startsWith(regla)) {
                    String restante = subcadena.substring(1);
                    logBuffer.append(padding).append("Paso ").append(paso++).append(": ").append(variableActual).append(" -> ").append(regla)
                            .append(". Consumido: '").append(regla).append("'. Restante: '").append(restante).append("'\n");

                    if (restante.isEmpty()) {
                        return true; // Éxito si solo queda el terminal y consume toda la cadena.
                    }
                    // Si hay un error aquí, es porque la GLC real requiere más complejidad (AP).
                }
            }
            // 2. Regla a variable o mixta (ej: AB o aB o ab)
            else {
                // Iterar sobre la regla para ver cuántos terminales consume
                int charsConsumed = 0;
                boolean match = true;

                for (int i = 0; i < regla.length(); i++) {
                    String simbolo = String.valueOf(regla.charAt(i));

                    if (terminales.contains(simbolo)) {
                        if (subcadena.length() > charsConsumed && subcadena.charAt(charsConsumed) == regla.charAt(i)) {
                            charsConsumed++;
                        } else {
                            match = false; // El terminal no coincide
                            break;
                        }
                    } else if (variables.contains(simbolo)) {
                        // Es una variable, necesitamos recursión
                        String subcadenaRestante = subcadena.substring(charsConsumed);
                        logBuffer.append(padding).append("Paso ").append(paso++).append(": ").append(variableActual).append(" -> ").append(regla)
                                .append(". Consumido (T): '").append(subcadena.substring(0, charsConsumed)).append("'. Próxima Variable: ").append(simbolo).append("\n");

                        if (backtrackParse(simbolo, subcadenaRestante, logBuffer, nivel + 1, pilaSimulada, paso)) {
                            return true; // Éxito a través de la recursión
                        }
                        match = false; // Falló la recursión, debe volver y probar otra regla
                        break;
                    }
                }

                // Si la regla era completamente de terminales y la consumió
                if (match && charsConsumed == subcadena.length() && charsConsumed > 0) {
                    logBuffer.append(padding).append("Paso ").append(paso++).append(": ").append(variableActual).append(" -> ").append(regla)
                            .append(". Cadena consumida completamente.\n");
                    return true;
                }
            }
        }

        return false;
    }
}