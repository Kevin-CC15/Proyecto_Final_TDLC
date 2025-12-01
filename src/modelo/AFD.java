package modelo;

import java.util.Map;
import java.util.Set;

/**
 * Representa un Autómata Finito Determinista (AFD).
 * Definido por la 5-tupla: (Q, Σ, δ, q0, F)
 */
public class AFD {

    // Q: Conjunto de estados
    private final Set<String> estados;

    // Σ: Alfabeto de entrada
    private final Set<String> alfabeto;

    // δ: Función de transición. Estructura: (q, a) -> q'
    // Map<EstadoActual, Map<Simbolo, EstadoSiguiente>>
    private final Map<String, Map<String, String>> transiciones;

    // q0: Estado inicial
    private final String estadoInicial;

    // F: Conjunto de estados finales
    private final Set<String> estadosFinales;

    /**
     * Constructor del AFD.
     */
    public AFD(Set<String> estados, Set<String> alfabeto,
               Map<String, Map<String, String>> transiciones,
               String estadoInicial, Set<String> estadosFinales) {

        this.estados = estados;
        this.alfabeto = alfabeto;
        this.transiciones = transiciones;
        this.estadoInicial = estadoInicial;
        this.estadosFinales = estadosFinales;

        // Aquí se pueden agregar más validaciones de consistencia del AFD
    }

    /**
     * Simula la ejecución del AFD con una cadena de entrada.
     * @param cadena La cadena a probar.
     * @param logBuffer Buffer para registrar el proceso paso a paso para la vista.
     * @return true si la cadena es aceptada, false si es rechazada.
     */
    public boolean simularCadena(String cadena, StringBuilder logBuffer) {
        logBuffer.append("--- INICIO DE SIMULACIÓN ---\n");
        logBuffer.append("Cadena a evaluar: ").append(cadena).append("\n");

        String estadoActual = estadoInicial;
        logBuffer.append("Paso 0: Estado inicial: ").append(estadoActual).append("\n");

        int paso = 1;

        // Iterar sobre cada símbolo de la cadena
        for (int i = 0; i < cadena.length(); i++) {
            char simboloChar = cadena.charAt(i);
            String simbolo = String.valueOf(simboloChar);

            // 1. Validar que el símbolo esté en el alfabeto
            if (!alfabeto.contains(simbolo)) {
                logBuffer.append("\n!!! ERROR !!!\n");
                logBuffer.append("El símbolo '").append(simbolo).append("' de la cadena no pertenece al alfabeto del AFD.\n");
                return false;
            }

            // 2. Buscar la función de transición δ(estadoActual, simbolo)
            Map<String, String> transicionesDesdeEstado = transiciones.get(estadoActual);

            if (transicionesDesdeEstado == null || !transicionesDesdeEstado.containsKey(simbolo)) {
                // No hay transición definida para (estadoActual, simbolo)
                logBuffer.append("\nEstado actual: ").append(estadoActual).append(", Símbolo leído: ").append(simbolo).append("\n");
                logBuffer.append("!!! RECHAZADA !!!\n");
                logBuffer.append("No existe transición definida para el par (").append(estadoActual).append(", ").append(simbolo).append(").\n");
                return false;
            }

            String estadoSiguiente = transicionesDesdeEstado.get(simbolo);

            // 3. Registrar y actualizar el estado
            logBuffer.append("Paso ").append(paso++).append(": Leer '").append(simbolo)
                    .append("'. Transición δ(").append(estadoActual).append(", ").append(simbolo)
                    .append(") -> ").append(estadoSiguiente).append("\n");

            estadoActual = estadoSiguiente;
        }

        logBuffer.append("\n--- FIN DE CADENA ---\n");
        logBuffer.append("Estado final alcanzado: ").append(estadoActual).append("\n");

        // 4. Determinar si el estado final alcanzado es un estado de aceptación
        boolean aceptada = estadosFinales.contains(estadoActual);

        if (aceptada) {
            logBuffer.append("El estado ").append(estadoActual).append(" es un estado FINAL (F).\n");
            logBuffer.append("RESULTADO: ACEPTADA\n");
        } else {
            logBuffer.append("El estado ").append(estadoActual).append(" NO es un estado final (F).\n");
            logBuffer.append("RESULTADO: RECHAZADA\n");
        }

        logBuffer.append("--------------------------\n");

        return aceptada;
    }
}