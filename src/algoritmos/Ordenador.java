package algoritmos;

import modelo.Personaje;
import java.util.ArrayList;
import java.util.List;

public class Ordenador {

    // DyC: Merge Sort O(n log n)
    public List<Personaje> ordenarPorMergeSort(List<Personaje> lista) {
        // Caso base: 1 o 0 elementos ya están ordenados
        if (lista.size() <= 1) {
            return lista;
        }

        // 1. Dividir: Partimos la lista a la mitad
        int medio = lista.size() / 2;
        List<Personaje> izquierda = new ArrayList<>(lista.subList(0, medio));
        List<Personaje> derecha = new ArrayList<>(lista.subList(medio, lista.size()));

        // 2. Conquistar: Ordenamos recursivamente ambas mitades
        izquierda = ordenarPorMergeSort(izquierda);
        derecha = ordenarPorMergeSort(derecha);

        // 3. Combinar: Intercalamos las mitades ordenadas
        return mezclar(izquierda, derecha);
    }

    private List<Personaje> mezclar(List<Personaje> izq, List<Personaje> der) {
        List<Personaje> resultado = new ArrayList<>();
        int i = 0, j = 0;

        while (i < izq.size() && j < der.size()) {
            // Usamos <= para mantener la ESTABILIDAD (respeta el orden de inserción)
            if (izq.get(i).getGenero().compareToIgnoreCase(der.get(j).getGenero()) <= 0) {
                resultado.add(izq.get(i));
                i++;
            } else {
                resultado.add(der.get(j));
                j++;
            }
        }

        while (i < izq.size()) {
            resultado.add(izq.get(i));
            i++;
        }
        while (j < der.size()) {
            resultado.add(der.get(j));
            j++;
        }
        return resultado;
    }

    // ORDENAMIENTO CUADRÁTICO: Bubble Sort O(n^2) (Para tabla comparativa del informe)
    public void ordenarPorBurbujeo(List<Personaje> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getGenero().compareToIgnoreCase(lista.get(j + 1).getGenero()) > 0) {
                    Personaje temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // Asigna el ID de forma autoincremental (1 a 23) tras haber ordenado por género
    public void asignarIdsAutoincrementales(List<Personaje> lista) {
        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).setId(i + 1);
        }
    }

    // Genera la medición en ms exigida por el profesor para la documentación
    public void mostrarTablaComparativa(List<Personaje> baseDesordenada) {
        List<Personaje> copiaMerge = new ArrayList<>(baseDesordenada);
        List<Personaje> copiaBubble = new ArrayList<>(baseDesordenada);

        long inicioMerge = System.nanoTime();
        ordenarPorMergeSort(copiaMerge);
        long finMerge = System.nanoTime();
        double tiempoMergeMs = (finMerge - inicioMerge) / 1_000_000.0;

        long inicioBubble = System.nanoTime();
        ordenarPorBurbujeo(copiaBubble);
        long finBubble = System.nanoTime();
        double tiempoBubbleMs = (finBubble - inicioBubble) / 1_000_000.0;

        System.out.println("\n================ TABLA COMPARATIVA DE RENDIMIENTO (n = 23) ================");
        System.out.printf("| %-25s | %-15s | %-25s |\n", "Algoritmo", "Complejidad", "Tiempo de Ejecución (ms)");
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("| %-25s | %-15s | %.5f ms                |\n", "Merge Sort (D&C)", "O(n log n)", tiempoMergeMs);
        System.out.printf("| %-25s | %-15s | %.5f ms                |\n", "Bubble Sort (Cuadrático)", "O(n^2)", tiempoBubbleMs);
        System.out.println("===========================================================================\n");
    }
}