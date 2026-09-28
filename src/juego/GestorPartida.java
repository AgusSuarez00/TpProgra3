package juego;

import algoritmos.MotorIA;
import algoritmos.Ordenador;
import modelo.Personaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GestorPartida {
    private List<Personaje> tableroOficial;
    private final Ordenador ordenador;
    private final MotorIA motorIA;
    private final Scanner scanner;
    private final Random random;

    public GestorPartida() {
        this.ordenador = new Ordenador();
        this.motorIA = new MotorIA();
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        inicializarTablero();
    }

    private void inicializarTablero() {
        List<Personaje> sinOrdenar = cargar23Personajes();

        ordenador.mostrarTablaComparativa(sinOrdenar);

        this.tableroOficial = ordenador.ordenarPorMergeSort(sinOrdenar);
        ordenador.asignarIdsAutoincrementales(this.tableroOficial);
    }

    private List<Personaje> cargar23Personajes() {
        List<Personaje> lista = new ArrayList<>();
        lista.add(new Personaje(0, "Ana", "Mujer", false, false, "negro"));
        lista.add(new Personaje(0, "Juan", "Hombre", false, false, "negro"));
        lista.add(new Personaje(0, "Maria", "Mujer", false, false, "colorado"));
        lista.add(new Personaje(0, "Pedro", "Hombre", false, false, "colorado"));
        lista.add(new Personaje(0, "Lucia", "Mujer", false, false, "amarillo"));
        lista.add(new Personaje(0, "Carlos", "Hombre", false, false, "amarillo"));
        lista.add(new Personaje(0, "Sofia", "Mujer", false, true, "negro"));
        lista.add(new Personaje(0, "Luis", "Hombre", false, true, "negro"));
        lista.add(new Personaje(0, "Marta", "Mujer", false, true, "colorado"));
        lista.add(new Personaje(0, "Diego", "Hombre", false, true, "colorado"));
        lista.add(new Personaje(0, "Elena", "Mujer", false, true, "amarillo"));
        lista.add(new Personaje(0, "Jorge", "Hombre", false, true, "amarillo"));
        lista.add(new Personaje(0, "Clara", "Mujer", true, false, "negro"));
        lista.add(new Personaje(0, "Raul", "Hombre", true, false, "negro"));
        lista.add(new Personaje(0, "Paula", "Mujer", true, false, "colorado"));
        lista.add(new Personaje(0, "Mario", "Hombre", true, false, "colorado"));
        lista.add(new Personaje(0, "Laura", "Mujer", true, false, "amarillo"));
        lista.add(new Personaje(0, "Pablo", "Hombre", true, false, "amarillo"));
        lista.add(new Personaje(0, "Carmen", "Mujer", true, true, "negro"));
        lista.add(new Personaje(0, "Andres", "Hombre", true, true, "negro"));
        lista.add(new Personaje(0, "Silvia", "Mujer", true, true, "colorado"));
        lista.add(new Personaje(0, "Fernando", "Hombre", true, true, "colorado"));
        lista.add(new Personaje(0, "Teresa", "Mujer", true, true, "amarillo"));
        return lista;
    }

    public void iniciarMenu() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n================ JUEGO ADIVINA QUIÉN (23 PERSONAJES) ================");
            System.out.println("1. Jugar Humano vs. Máquina");
            System.out.println("2. Modo Máquina vs. Máquina (Presenciar procesos algorítmicos)");
            System.out.println("3. Ver lista de los 23 personajes ordenados (Merge Sort)");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero();
            switch (opcion) {
                case 1 -> jugarHumanoVsMaquina();
                case 2 -> jugarMaquinaVsMaquina();
                case 3 -> mostrarTablero(tableroOficial);
                case 4 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void jugarHumanoVsMaquina() {
        mostrarTablero(tableroOficial);
        Personaje secretoMaquina = tableroOficial.get(random.nextInt(tableroOficial.size()));

        Personaje secretoHumano = null;
        while (secretoHumano == null) {
            System.out.print("\nElige el ID de tu personaje secreto (1 a 23): ");
            int id = leerEntero();
            // Usamos Búsqueda Binaria (Divide y Conquista) para buscar el ID seleccionado
            secretoHumano = motorIA.buscarPorIdBinaria(tableroOficial, id);
            if (secretoHumano == null) {
                System.out.println("ID no encontrado. Intenta entre 1 y 23.");
            }
        }
        System.out.println("Has elegido en secreto a: " + secretoHumano.getNombre() + " (La máquina NO tiene acceso a esta variable).");

        List<Personaje> sospechososDelHumano = new ArrayList<>(tableroOficial);
        List<Personaje> sospechososDeLaMaquina = new ArrayList<>(tableroOficial);
        List<String> preguntasHechasPorMaquina = new ArrayList<>();

        boolean partidaTerminada = false;
        int turno = 1;

        while (!partidaTerminada) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("                         TURNO " + turno);
            System.out.println("------------------------------------------------------------");

            // TURNO DEL HUMANO
            System.out.println("[Tu Turno] Te quedan " + sospechososDelHumano.size() + " sospechosos posibles:");
            for (Personaje p : sospechososDelHumano) {
                System.out.print("[" + p.getId() + ":" + p.getNombre() + "] ");
            }
            System.out.println("\n\n¿Qué deseas hacer?");
            System.out.println("1. Filtrar por Género (Hombre/Mujer)");
            System.out.println("2. Filtrar por Calvicie (Sí/No)");
            System.out.println("3. Filtrar por Lentes (Sí/No)");
            System.out.println("4. Filtrar por Color de Pelo (colorado/negro/amarillo)");
            System.out.println("5. Pedir Comodín Divide y Conquista (Divide tablero en Grupo A / B)");
            System.out.println("6. Arriesgar nombre o ID del personaje");
            System.out.print("Opción: ");

            int accion = leerEntero();
            String filtroHumano = null;

            switch (accion) {
                case 1 -> {
                    System.out.print("Ingresa género a consultar (Hombre/Mujer): ");
                    filtroHumano = "GENERO:" + scanner.nextLine().trim();
                }
                case 2 -> filtroHumano = "CALVICIE:true";
                case 3 -> filtroHumano = "LENTES:true";
                case 4 -> {
                    System.out.print("Ingresa color de pelo (colorado/negro/amarillo): ");
                    filtroHumano = "PELO:" + scanner.nextLine().trim();
                }
                case 5 -> {
                    sospechososDelHumano = motorIA.darPistaDivideYConquista(sospechososDelHumano, secretoMaquina);
                    System.out.println("-> Tu lista de sospechosos se redujo a la mitad (" + sospechososDelHumano.size() + " restantes).");
                }
                case 6 -> {
                    System.out.print("Ingresa el NOMBRE del personaje que crees que tiene la máquina: ");
                    String intento = scanner.nextLine().trim();
                    // Usamos la Búsqueda Lineal O(n) para buscar el objeto por su String nombre
                    Personaje personajeArriesgado = motorIA.buscarPorNombreLineal(tableroOficial, intento);

                    if (personajeArriesgado != null && personajeArriesgado.getId() == secretoMaquina.getId()) {
                        System.out.println("\n¡¡¡GANASTE!!! Adivinaste que el personaje secreto de la máquina era " + secretoMaquina.getNombre() + ".");
                        partidaTerminada = true;
                    } else {
                        System.out.println("-> Incorrecto. No es " + intento + ". Pierdes tu turno.");
                    }
                }
                default -> System.out.println("Opción no válida, pierdes el turno.");
            }

            if (filtroHumano != null && !partidaTerminada) {
                boolean cumple = motorIA.cumpleFiltro(secretoMaquina, filtroHumano);
                System.out.println("-> La Máquina responde: " + (cumple ? "¡SÍ!" : "NO."));
                sospechososDelHumano = motorIA.filtrarSospechosos(sospechososDelHumano, filtroHumano, cumple);
                System.out.println("-> Tu lista de sospechosos se redujo a " + sospechososDelHumano.size() + " personajes.");
            }

            if (partidaTerminada) break;

            // TURNO DE LA MÁQUINA (Estrategia Greedy)
            System.out.println("\n[Turno de la Máquina]");
            // Elemento 5 Greedy: Función solución / Criterio de parada (si queda 1 sospechoso, arriesga)
            if (sospechososDeLaMaquina.size() == 1) {
                Personaje elegido = sospechososDeLaMaquina.get(0);
                System.out.println("¡Criterio de parada alcanzado! Solo queda 1 candidato.");
                System.out.println("La Máquina arriesga directamente: ¿Tu personaje es " + elegido.getNombre() + "?");
                if (elegido.getId() == secretoHumano.getId()) {
                    System.out.println("-> ¡LA MÁQUINA GANÓ LA PARTIDA! Adivinó a " + secretoHumano.getNombre() + ".");
                } else {
                    System.out.println("-> La Máquina falló.");
                }
                partidaTerminada = true;
            } else {
                String mejorFiltro = motorIA.seleccionarPreguntaGreedy(sospechososDeLaMaquina, preguntasHechasPorMaquina, true);
                if (mejorFiltro != null) {
                    preguntasHechasPorMaquina.add(mejorFiltro);
                    System.out.println("-> Decisión Greedy seleccionada: " + motorIA.formatearPreguntaLegible(mejorFiltro));

                    // El jugador responde honestamente según su carta secreta (encapsulada)
                    boolean respuestaHumano = motorIA.cumpleFiltro(secretoHumano, mejorFiltro);
                    System.out.println("-> Respuesta recibida: " + (respuestaHumano ? "SÍ" : "NO"));

                    sospechososDeLaMaquina = motorIA.filtrarSospechosos(sospechososDeLaMaquina, mejorFiltro, respuestaHumano);
                    System.out.println("-> A la máquina le quedan " + sospechososDeLaMaquina.size() + " sospechosos posibles.");
                }
            }
            turno++;
        }
    }

    // MODO MÁQUINA VS MÁQUINA: Muestra todos los procesos internos paso a paso
    private void jugarMaquinaVsMaquina() {
        System.out.println("\n================ INICIANDO SIMULACIÓN MÁQUINA VS. MÁQUINA ================");
        System.out.println("* Máquina 1 (Inteligente): Usa Greedy Balanceado (busca partición óptima 50/50 y arriesga con certeza).");
        System.out.println("* Máquina 2 (Ineficiente): Usa Greedy Defectuoso (elige filtros minoritarios y arriesga apurada con <= 4 sospechosos).");

        Personaje secretoDeM1 = tableroOficial.get(random.nextInt(tableroOficial.size()));
        Personaje secretoDeM2 = tableroOficial.get(random.nextInt(tableroOficial.size()));

        System.out.println("\n[Setup Secreto] Máquina 1 escondió a: " + secretoDeM1.getNombre());
        System.out.println("[Setup Secreto] Máquina 2 escondió a: " + secretoDeM2.getNombre());

        List<Personaje> sospechososM1 = new ArrayList<>(tableroOficial);
        List<Personaje> sospechososM2 = new ArrayList<>(tableroOficial);
        List<String> historialM1 = new ArrayList<>();
        List<String> historialM2 = new ArrayList<>();

        boolean fin = false;
        int turno = 1;

        while (!fin) {
            System.out.println("\n============================================================");
            System.out.println("                    TURNO DE SIMULACIÓN " + turno);
            System.out.println("============================================================");

            // JUEGA MÁQUINA 1 (Inteligente: adivina el secretoDeM2)
            System.out.println("\n>>> [Turno Máquina 1 - Inteligente] (Sospechosos actuales: " + sospechososM1.size() + ")");
            if (sospechososM1.size() == 1) {
                Personaje apuesta = sospechososM1.get(0);
                System.out.println("   [Criterio de Parada M1] Espacio reducido a 1 único candidato.");
                System.out.println("   Máquina 1 arriesga: ¡El personaje de M2 es " + apuesta.getNombre() + "!");
                if (apuesta.getId() == secretoDeM2.getId()) {
                    System.out.println("\n🏆 ¡MÁQUINA 1 (INTELIGENTE) GANÓ LA PARTIDA EN " + turno + " TURNOS!");
                    fin = true;
                    break;
                }
            } else {
                String filtroM1 = motorIA.seleccionarPreguntaGreedy(sospechososM1, historialM1, true);
                historialM1.add(filtroM1);
                boolean respM2 = motorIA.cumpleFiltro(secretoDeM2, filtroM1);
                System.out.println("   Máquina 1 pregunta: " + motorIA.formatearPreguntaLegible(filtroM1) + " -> M2 responde: " + (respM2 ? "SÍ" : "NO"));
                sospechososM1 = motorIA.filtrarSospechosos(sospechososM1, filtroM1, respM2);
                System.out.print("   Sospechosos restantes para M1 (" + sospechososM1.size() + "): ");
                imprimirNombres(sospechososM1);
            }

            // JUEGA MÁQUINA 2 (Ineficiente/Impulsiva: adivina el secretoDeM1)
            System.out.println("\n>>> [Turno Máquina 2 - Ineficiente] (Sospechosos actuales: " + sospechososM2.size() + ")");
            // Criterio de parada precipitado de M2: si le quedan 4 o menos, se la juega al azar entre los que le quedan
            if (sospechososM2.size() <= 4) {
                Personaje apuestaM2 = sospechososM2.get(random.nextInt(sospechososM2.size()));
                System.out.println("   [Criterio Impulsivo M2] Quedan " + sospechososM2.size() + " sospechosos y decide arriesgar sin seguridad: ¿Es " + apuestaM2.getNombre() + "?");
                if (apuestaM2.getId() == secretoDeM1.getId()) {
                    System.out.println("\n🏆 ¡MÁQUINA 2 GANÓ DE PURA SUERTE EN EL TURNO " + turno + "!");
                    fin = true;
                    break;
                } else {
                    System.out.println("   -> ¡Falló! No era " + apuestaM2.getNombre() + ". Lo elimina de su lista y pierde el turno.");
                    sospechososM2.remove(apuestaM2);
                }
            } else {
                String filtroM2 = motorIA.seleccionarPreguntaGreedy(sospechososM2, historialM2, false);
                if (filtroM2 != null) {
                    historialM2.add(filtroM2);
                    boolean respM1 = motorIA.cumpleFiltro(secretoDeM1, filtroM2);
                    System.out.println("   Máquina 2 pregunta: " + motorIA.formatearPreguntaLegible(filtroM2) + " -> M1 responde: " + (respM1 ? "SÍ" : "NO"));
                    sospechososM2 = motorIA.filtrarSospechosos(sospechososM2, filtroM2, respM1);
                    System.out.print("   Sospechosos restantes para M2 (" + sospechososM2.size() + "): ");
                    imprimirNombres(sospechososM2);
                }
            }
            turno++;
        }
    }

    private void mostrarTablero(List<Personaje> lista) {
        System.out.println("\n--- LISTA DE 23 PERSONAJES (ORDENADOS POR GÉNERO Y CON ID AUTOINCREMENTAL) ---");
        for (Personaje p : lista) {
            System.out.println(p);
        }
    }

    private void imprimirNombres(List<Personaje> lista) {
        for (Personaje p : lista) {
            System.out.print("[" + p.getNombre() + "] ");
        }
        System.out.println();
    }

    private int leerEntero() {
        while (!scanner.hasNextInt()) {
            System.out.print("Por favor, ingresa un número válido: ");
            scanner.next();
        }
        int num = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        return num;
    }
    public List<Personaje> getTableroOficial() {
        return tableroOficial;
    }
    public void ejecutarSimulacionDesdeUI() {
        jugarMaquinaVsMaquina();
    }
}