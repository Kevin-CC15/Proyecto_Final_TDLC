package modelo;

import java.util.*;

/**
 * Clase que representa un Autómata con Pila (AP).
 * * MODIFICADO:
 * 1. Soporta formato de transición de clase: (q,a,X)(p,Y)
 * 2. Soporta inicio con pila vacía si Z0 = λ.
 * 3. Soporta λ en la entrada de pila para NO hacer pop (comodín).
 */
public class AutomataPila {
    private final Set<String> Q;
    private final Set<String> Sigma;
    private final Set<String> Gamma;
    private final String q0;
    private final String Z0;
    private final Set<String> F;
    private final Map<String, Set<TransicionAP>> Delta; // Clave: Estado actual

    // Constante para representar la cadena vacía (Lambda/Epsilon)
    public static final String LAMBDA = "λ";

    /**
     * Constructor del Automata de Pila.
     */
    public AutomataPila(Set<String> Q, Set<String> Sigma, Set<String> Gamma,
                        Map<String, Set<TransicionAP>> Delta, String q0,
                        String Z0, Set<String> F) {
        this.Q = Q;
        this.Sigma = Sigma;
        this.Gamma = Gamma;
        this.Delta = Delta;
        this.q0 = q0;
        this.Z0 = Z0;
        this.F = F;
    }

    /**
     * Simula la ejecución del AP con una cadena de entrada.
     */
    public boolean simularCadena(String cadena, StringBuilder logBuffer) {
        logBuffer.setLength(0);
        logBuffer.append("--- INICIO DE SIMULACIÓN AP (Aceptación por F y Pila Vacía) ---\n");
        logBuffer.append("Cadena a probar: ").append(cadena.isEmpty() ? LAMBDA : cadena).append("\n");

        // Inicialización de la pila
        Stack<String> pilaInicial = new Stack<>();

        // LÓGICA DE PILA VACÍA: Solo inicializa la pila con Z0 si Z0 NO es LAMBDA
        // Esto permite cumplir con el requisito de tu clase donde la pila inicia vacía.
        if (!Z0.trim().isEmpty() && !Z0.equals(LAMBDA)) {
            logBuffer.append("Símbolo Inicial de Pila (Z0): ").append(Z0).append(". Pila inicial: [").append(Z0).append("]\n");
            pilaInicial.push(Z0);
        } else {
            logBuffer.append("Símbolo Inicial de Pila (Z0): ").append(LAMBDA).append(" (Pila inicia vacía).\n");
        }

        // Llamada a la simulación recursiva
        boolean aceptada = backtrackSimulacion(q0, cadena, pilaInicial, logBuffer, 0);

        logBuffer.append("\n--- FIN DE SIMULACIÓN ---\n");
        logBuffer.append("RESULTADO: ").append(aceptada ? "ACEPTADA" : "RECHAZADA").append("\n");
        logBuffer.append("---------------------------------------------------\n");

        return aceptada;
    }

    /**
     * Algoritmo de simulación recursivo (backtracking) para AP No Determinista.
     */
    private boolean backtrackSimulacion(String estadoActual, String subcadena, Stack<String> pila, StringBuilder logBuffer, int nivel) {
        String padding = " ".repeat(nivel * 2);

        // 1. CONDICIÓN DE ACEPTACIÓN
        // Cadena Consumida Y Pila Vacía Y Estado Final.
        if (subcadena.isEmpty() && pila.isEmpty() && F.contains(estadoActual)) {
            logBuffer.append(padding).append("-> ACEPTADO: Cadena terminada, Pila vacía, Estado (").append(estadoActual).append(") es Final.\n");
            return true;
        }

        // 2. Transiciones posibles desde el estado actual
        Set<TransicionAP> transicionesPosibles = Delta.getOrDefault(estadoActual, Collections.emptySet());

        // 3. Iterar sobre transiciones
        for (TransicionAP t : transicionesPosibles) {
            String simboloEntrada = t.simboloEntrada;
            String simboloTope = t.simboloTope;

            // a) ¿Coincide el símbolo de entrada?
            boolean consumeSimbolo = !simboloEntrada.equals(LAMBDA) && subcadena.startsWith(simboloEntrada);
            boolean isLambdaInput = simboloEntrada.equals(LAMBDA);

            if (consumeSimbolo || isLambdaInput) {

                // b) Lógica de coincidencia de tope de pila (X):

                // 1. Coincidencia específica (X != λ): Se requiere que la pila NO esté vacía y que el tope sea X.
                boolean matchSpecificPop = !simboloTope.equals(LAMBDA) && !pila.isEmpty() && pila.peek().equals(simboloTope);

                // 2. Coincidencia λ (X = λ): Significa "no me importa el tope de la pila" o "la pila debe estar vacía".
                // IMPORTANTE: En tu clase esto significa "no hacer pop", simplemente ignorar la pila.
                boolean matchLambdaPop = simboloTope.equals(LAMBDA);

                // La transición es aplicable si hay coincidencia específica o si no requiere tope específico (matchLambdaPop)
                if (matchSpecificPop || matchLambdaPop) {

                    // Si se pide sacar un símbolo específico y la pila está vacía, no se puede aplicar.
                    if (matchSpecificPop && pila.isEmpty()) continue;

                    // Simulación de la transición
                    Stack<String> nuevaPila = (Stack<String>) pila.clone();
                    String nuevaSubcadena = subcadena;
                    String pilaTopeStr = pila.isEmpty() ? LAMBDA : pila.peek();

                    // I. Consumir entrada
                    if (consumeSimbolo) {
                        nuevaSubcadena = subcadena.substring(simboloEntrada.length());
                    }

                    // II. Modificar pila (Sacar: X)
                    // Solo hacemos POP si X NO es λ. Si X es λ, la pila se queda igual (comportamiento de tu clase).
                    if (matchSpecificPop) {
                        nuevaPila.pop();
                    }

                    // III. Modificar pila (Meter: YZ)
                    String reemplazoPila = t.reemplazoPila;
                    if (!reemplazoPila.equals(LAMBDA)) {
                        // Meter de derecha a izquierda
                        for (int i = reemplazoPila.length() - 1; i >= 0; i--) {
                            nuevaPila.push(String.valueOf(reemplazoPila.charAt(i)));
                        }
                    }

                    // LOG
                    logBuffer.append(padding).append("Paso: (").append(estadoActual).append(", '").append(simboloEntrada).append("', '").append(simboloTope)
                            .append("') [Tope: ").append(pilaTopeStr).append("] -> (").append(t.estadoSiguiente).append(", '").append(t.reemplazoPila).append("'). Pila: ").append(pila).append(" -> ").append(nuevaPila).append("\n");

                    // Llamada recursiva
                    if (backtrackSimulacion(t.estadoSiguiente, nuevaSubcadena, nuevaPila, logBuffer, nivel + 1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Parsea la cadena de texto de transiciones a la estructura de datos Delta.
     * SOPORTA DOS FORMATOS:
     * 1. Clásico: q,a,X=p,YZ
     * 2. Formato de tu Clase: (q,a,X)(p,YZ)
     */
    public static Map<String, Set<TransicionAP>> parseRawTransiciones(String rawTransiciones,
                                                                      Set<String> estados, Set<String> alfabeto, Set<String> alfabetoPila) {
        Map<String, Set<TransicionAP>> delta = new HashMap<>();
        String[] lineas = rawTransiciones.split("\\n");
        String lambda = LAMBDA;

        for (String line : lineas) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;

            // --- ADAPTACIÓN DE FORMATO (q,a,X)(p,Y) ---
            // Detectar si usa el formato de paréntesis y convertirlo internamente
            if (trimmedLine.startsWith("(") && trimmedLine.endsWith(")")) {
                // Reemplazar la unión central ")( " o ")(" por "="
                trimmedLine = trimmedLine.replace(")(", "=");
                trimmedLine = trimmedLine.replace(") (", "=");
                // Quitar paréntesis exteriores
                trimmedLine = trimmedLine.replace("(", "").replace(")", "");
            }
            // ------------------------------------------

            try {
                // 1. Separar: "q,a,X" y "p,YZ"
                String[] partes = trimmedLine.split("=");
                if (partes.length != 2) throw new IllegalArgumentException("El formato debe ser (q,a,X)(p,YZ) o q,a,X=p,YZ.");

                // 2. Parsear el lado izquierdo: q,a,X
                String[] izq = partes[0].split(",");
                if (izq.length != 3) throw new IllegalArgumentException("El lado izquierdo debe tener 3 componentes (Estado, Entrada, TopePila).");
                String q = izq[0].trim();
                String a = izq[1].trim();
                String X = izq[2].trim();

                // 3. Parsear el lado derecho: p,YZ
                String[] der = partes[1].split(",");
                if (der.length != 2) throw new IllegalArgumentException("El lado derecho debe tener 2 componentes (NuevoEstado, NuevoTope).");
                String p = der[0].trim();
                String YZ = der[1].trim();

                // Validaciones de consistencia
                if (!estados.contains(q) || !estados.contains(p)) {
                    throw new IllegalArgumentException("Estado '" + q + "' o '" + p + "' no está en Q.");
                }
                if (!a.equals(lambda) && !alfabeto.contains(a)) {
                    throw new IllegalArgumentException("Símbolo de entrada '" + a + "' no está en Σ.");
                }

                // NOTA IMPORTANTE: Permitimos que X sea lambda aunque no esté en el alfabeto de pila
                // para soportar el comportamiento de "wildcard" (no mirar la pila).
                if (!X.equals(lambda) && !alfabetoPila.contains(X)) {
                    throw new IllegalArgumentException("Símbolo de pila '" + X + "' no está en Γ.");
                }

                // Validar reemplazo
                if (!YZ.equals(lambda)) {
                    for (char c : YZ.toCharArray()) {
                        String simbolo = String.valueOf(c);
                        if (!alfabetoPila.contains(simbolo)) {
                            throw new IllegalArgumentException("Símbolo '" + simbolo + "' en reemplazo no está en Γ.");
                        }
                    }
                }

                // 5. Crear y añadir la transición
                TransicionAP nuevaTransicion = new TransicionAP(q, a, X, p, YZ);
                delta.putIfAbsent(q, new HashSet<>());
                delta.get(q).add(nuevaTransicion);

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en transición: '" + trimmedLine + "'. " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException("Error de sintaxis desconocido: '" + trimmedLine + "'. Revise comas y paréntesis.");
            }
        }
        return delta;
    }

    /**
     * Clase interna para representar una Transición en el AP.
     */
    public static class TransicionAP {
        final String estadoActual;
        final String simboloEntrada;
        final String simboloTope;
        final String estadoSiguiente;
        final String reemplazoPila;

        public TransicionAP(String estadoActual, String simboloEntrada, String simboloTope, String estadoSiguiente, String reemplazoPila) {
            this.estadoActual = estadoActual;
            this.simboloEntrada = simboloEntrada;
            this.simboloTope = simboloTope;
            this.estadoSiguiente = estadoSiguiente;
            this.reemplazoPila = reemplazoPila;
        }

        @Override
        public String toString() {
            return "(" + estadoActual + ", " + simboloEntrada + ", " + simboloTope + ") -> (" + estadoSiguiente + ", " + reemplazoPila + ")";
        }
    }
}