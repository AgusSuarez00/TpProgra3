package algoritmos;

import modelo.Personaje;
import java.util.ArrayList;
import java.util.List;

public class MotorIA {


    // 1. DIVIDE Y CONQUISTA: Búsqueda Binaria por ID O(log n)
    public Personaje buscarPorIdBinaria(List<Personaje> listaOrdenada, int idBuscado) {
        int ini = 0;
        int fin = listaOrdenada.size() - 1;

        while (ini <= fin) {
            int mid = (ini + fin) / 2;
            int idMedio = listaOrdenada.get(mid).getId();

            if (idMedio == idBuscado) {
                return listaOrdenada.get(mid);
            } else if (idBuscado < idMedio) {
                fin = mid - 1;
            } else {
                ini = mid + 1;
            }
        }
        return null;
    }

    // 1.B. BÚSQUEDA LINEAL DIRECTA O(n) - Para comparar contra la Búsqueda Binaria O(log n)
    public Personaje buscarPorNombreLineal(List<Personaje> lista, String nombreBuscado) {
        for (Personaje p : lista) {
            if (p.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return p; // Encontrado recorriendo uno por uno
            }
        }
        return null;
    }

    // 2. DIVIDE Y CONQUISTA: Comodín de ayuda (Parte los sospechosos RESTANTES en Grupo A y Grupo B y descarta la mitad)
    public List<Personaje> darPistaDivideYConquista(List<Personaje> sospechososActuales, Personaje secretoMaquina) {
        if (sospechososActuales.size() <= 1) {
            return sospechososActuales; // Caso base: ya queda solo 1
        }

        // DIVIDIR: Partimos la lista de los sospechosos QUE QUEDAN exactamente a la mitad
        int mitad = sospechososActuales.size() / 2;
        List<Personaje> grupoA = new ArrayList<>(sospechososActuales.subList(0, mitad));
        List<Personaje> grupoB = new ArrayList<>(sospechososActuales.subList(mitad, sospechososActuales.size()));

        int primerIdA = grupoA.get(0).getId();
        int ultimoIdA = grupoA.get(grupoA.size() - 1).getId();
        int primerIdB = grupoB.get(0).getId();
        int ultimoIdB = grupoB.get(grupoB.size() - 1).getId();

        System.out.println("[Comodín D&C] Dividiendo los " + sospechososActuales.size() + " sospechosos restantes en dos mitades...");

        // CONQUISTAR: Como siguen ordenados por ID, comparamos contra el límite del Grupo A en O(1)
        if (secretoMaquina.getId() <= ultimoIdA) {
            System.out.println("-> PISTA: El secreto está en el GRUPO A (IDs entre " + primerIdA + " y " + ultimoIdA + "). Se descarta el Grupo B.");
            return grupoA; // Descarta el Grupo B
        } else {
            System.out.println("-> PISTA: El secreto está en el GRUPO B (IDs entre " + primerIdB + " y " + ultimoIdB + "). Se descarta el Grupo A.");
            return grupoB; // Descarta el Grupo A
        }
    }

    // 3. ALGORITMO GREEDY: Selección voraz de la mejor pregunta en el turno
    public String seleccionarPreguntaGreedy(List<Personaje> sospechosos, List<String> preguntasRealizadas, boolean modoInteligente) {
        // Elemento 1 Greedy: Conjunto de candidatos
        String[] candidatos = {
                "GENERO:Hombre",
                "CALVICIE:true",
                "LENTES:true",
                "PELO:colorado",
                "PELO:negro",
                "PELO:amarillo"
        };

        String mejorCandidato = null;
        double mitadIdeal = sospechosos.size() / 2.0;
        double mejorDistanciaAMitad = modoInteligente ? Double.MAX_VALUE : -1.0;

        System.out.println("   [Pensamiento IA - Greedy] Analizando " + sospechosos.size() + " sospechosos restantes...");

        for (String candidato : candidatos) {
            // Elemento 2 Greedy: Función de factibilidad (que no se haya preguntado y que descarte al menos a 1)
            if (preguntasRealizadas.contains(candidato)) {
                continue;
            }

            int coinciden = contarCoincidencias(sospechosos, candidato);
            if (coinciden == 0 || coinciden == sospechosos.size()) {
                // No es factible porque descartaría 0 personajes en uno de los escenarios
                continue;
            }

            // Elemento 3 Greedy: Función de selección
            double distanciaAMitad = Math.abs(coinciden - mitadIdeal);
            System.out.printf("   -> Evaluando filtro [%-15s]: %2d cumplen / %2d no cumplen (Distancia al 50%%: %.1f)\n",
                    candidato, coinciden, (sospechosos.size() - coinciden), distanciaAMitad);

            if (modoInteligente) {
                // Máquina Inteligente: Busca minimizar la distancia al 50% (partición equilibrada)
                if (distanciaAMitad < mejorDistanciaAMitad) {
                    mejorDistanciaAMitad = distanciaAMitad;
                    mejorCandidato = candidato;
                }
            } else {
                // Máquina Ineficiente: Mal criterio Greedy (elige el filtro más desbalanceado/arriesgado)
                if (distanciaAMitad > mejorDistanciaAMitad) {
                    mejorDistanciaAMitad = distanciaAMitad;
                    mejorCandidato = candidato;
                }
            }
        }

        return mejorCandidato;
    }

    public int contarCoincidencias(List<Personaje> lista, String filtro) {
        int contador = 0;
        for (Personaje p : lista) {
            if (cumpleFiltro(p, filtro)) {
                contador++;
            }
        }
        return contador;
    }

    public boolean cumpleFiltro(Personaje p, String filtro) {
        String[] partes = filtro.split(":");
        String tipo = partes[0];
        String valor = partes[1];

        switch (tipo) {
            case "GENERO":
                return p.getGenero().equalsIgnoreCase(valor);
            case "CALVICIE":
                return p.isEsCalvo() == Boolean.parseBoolean(valor);
            case "LENTES":
                return p.isTieneLentes() == Boolean.parseBoolean(valor);
            case "PELO":
                return p.getColorPelo().equalsIgnoreCase(valor);
            default:
                return false;
        }
    }

    // Filtra y reduce el espacio de búsqueda según la respuesta obtenida (Sí/No)
    public List<Personaje> filtrarSospechosos(List<Personaje> actuales, String filtro, boolean respuestaExitosa) {
        List<Personaje> restantes = new ArrayList<>();
        for (Personaje p : actuales) {
            if (cumpleFiltro(p, filtro) == respuestaExitosa) {
                restantes.add(p);
            }
        }
        return restantes;
    }

    public String formatearPreguntaLegible(String filtro) {
        String[] partes = filtro.split(":");
        switch (partes[0]) {
            case "GENERO": return "¿El personaje es " + partes[1] + "?";
            case "CALVICIE": return "¿El personaje es calvo?";
            case "LENTES": return "¿El personaje usa lentes?";
            case "PELO": return "¿Tiene el pelo color " + partes[1] + "?";
            default: return filtro;
        }
    }
}