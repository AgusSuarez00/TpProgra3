package ui;

import algoritmos.MotorIA;
import juego.GestorPartida;
import modelo.Personaje;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VentanaJuego extends JFrame {

    // Referencias a nuestras clases lógicas (Principio SOLID: la UI solo delega)
    private final GestorPartida gestor;
    private final MotorIA motorIA;
    private final List<Personaje> todosLosPersonajes;

    // Variables de estado de la partida visual
    private Personaje secretoHumano;
    private Personaje secretoMaquina;
    private List<Personaje> sospechososDelHumano;
    private List<Personaje> sospechososDeLaMaquina;
    private List<String> preguntasHechasPorMaquina;

    // ELEMENTOS DE SWING (Con nombres claros como pidió el profesor)
    private JPanel panelPrincipal;      // El panel padre que usa CardLayout
    private JPanel panelMenu;           // Carta 1: Pantalla de inicio
    private JPanel panelTableroJuego;   // Carta 2: Pantalla donde están las 23 tarjetas

    private JLabel etiquetaEstado;      // Texto arriba que guía al jugador
    private JTextArea areaConsolaVisual;// Muestra qué respondió la máquina
    private JButton[] botonesTarjetas;  // Los 23 botones/tarjetas de los personajes

    public VentanaJuego(GestorPartida gestor) {
        this.gestor = gestor;
        this.motorIA = new MotorIA();
        this.todosLosPersonajes = gestor.getTableroOficial();

        configurarVentana();
        inicializarComponentesUI();
    }

    // PREGUNTA DEL PROFESOR: ¿Dónde se setea el tamaño/resolución de la ventana? -> Acá en setSize()
    private void configurarVentana() {
        setTitle("Adivina Quién - Programación III (23 Personajes)");
        setSize(1050, 720); // Resolución de la ventana (ancho x alto)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra el programa al tocar la X
        setLocationRelativeTo(null); // Centra la ventana en el monitor
    }

    private void inicializarComponentesUI() {
        // 1. Creamos el PRIMER PANEL (Contenedor Principal) con CardLayout
        panelPrincipal = new JPanel(new CardLayout());

        // 2. Armamos las dos pantallas (hojas) y las metemos adentro del panelPrincipal
        crearPanelMenu();
        crearPanelTableroJuego();

        panelPrincipal.add(panelMenu, "PANTALLA_MENU");
        panelPrincipal.add(panelTableroJuego, "PANTALLA_JUEGO");

        // Agregamos el panel principal al JFrame
        add(panelPrincipal);
    }

    // PANTALLA 1: MENÚ PRINCIPAL
    private void crearPanelMenu() {
        panelMenu = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel titulo = new JLabel("JUEGO ADIVINA QUIÉN (23 PERSONAJES)");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JButton btnJugarContraMaquina = new JButton("1. Jugar Humano vs. Máquina");
        btnJugarContraMaquina.setPreferredSize(new Dimension(340, 45));

        JButton btnMaquinaVsMaquina = new JButton("2. Simular Máquina vs. Máquina (Ver en Consola)");
        btnMaquinaVsMaquina.setPreferredSize(new Dimension(340, 45));

        // PATRÓN OBSERVADOR (ActionListener con función Lambda 'e ->')
        btnJugarContraMaquina.addActionListener(e -> iniciarPartidaVisual());

        btnMaquinaVsMaquina.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Se ejecutará la simulación Máquina vs. Máquina.\n" +
                            "Mira la consola inferior de IntelliJ para ver el proceso Greedy paso a paso.");
            gestor.ejecutarSimulacionDesdeUI();
        });

        gbc.gridy = 0; panelMenu.add(titulo, gbc);
        gbc.gridy = 1; panelMenu.add(btnJugarContraMaquina, gbc);
        gbc.gridy = 2; panelMenu.add(btnMaquinaVsMaquina, gbc);
    }

    // PANTALLA 2: TABLERO CON LAS 23 TARJETAS Y LOS BOTONES DE FILTRO
    private void crearPanelTableroJuego() {
        panelTableroJuego = new JPanel(new BorderLayout(10, 10));

        // Parte de arriba: cartel de instrucciones
        etiquetaEstado = new JLabel("PASO 1: Haz clic en una de las 23 tarjetas para elegir tu PERSONAJE SECRETO.", SwingConstants.CENTER);
        etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 16));
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTableroJuego.add(etiquetaEstado, BorderLayout.NORTH);

        // Parte central: Grilla con las 23 tarjetas de personajes
        JPanel grillaTarjetas = new JPanel(new GridLayout(4, 6, 8, 8));
        botonesTarjetas = new JButton[todosLosPersonajes.size()];

        for (int i = 0; i < todosLosPersonajes.size(); i++) {
            Personaje p = todosLosPersonajes.get(i);
            String textoTarjeta = "<html><center><b>#" + p.getId() + " - " + p.getNombre() + "</b><br>"
                    + p.getGenero() + "<br>"
                    + "Pelo: " + p.getColorPelo() + "<br>"
                    + (p.isEsCalvo() ? "Calvo" : "No calvo") + " | "
                    + (p.isTieneLentes() ? "Con Lentes" : "Sin Lentes")
                    + "</center></html>";

            JButton btnCarta = new JButton(textoTarjeta);
            btnCarta.addActionListener(e -> alHacerClicEnTarjeta(p));
            botonesTarjetas[i] = btnCarta;
            grillaTarjetas.add(btnCarta);
        }
        panelTableroJuego.add(grillaTarjetas, BorderLayout.CENTER);

        // Parte de abajo: Botones de filtros aplicables y registro de jugadas
        JPanel panelInferior = new JPanel(new BorderLayout());
        JPanel panelBotonesPreguntas = new JPanel(new GridLayout(2, 5, 5, 5));

        String[] filtros = {
                "GENERO:Hombre", "GENERO:Mujer", "CALVICIE:true",
                "LENTES:true", "PELO:negro", "PELO:colorado", "PELO:amarillo"
        };

        for (String filtro : filtros) {
            JButton btnFiltro = new JButton(motorIA.formatearPreguntaLegible(filtro));
            btnFiltro.addActionListener(e -> hacerPreguntaAlOponente(filtro));
            panelBotonesPreguntas.add(btnFiltro);
        }

        // Botón del comodín Divide y Conquista (Grupo A / Grupo B)
        JButton btnComodin = new JButton("Comodín D&C (Grupo A/B)");
        btnComodin.addActionListener(e -> usarComodinDivideYConquista());
        panelBotonesPreguntas.add(btnComodin);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> cambiarPantalla("PANTALLA_MENU"));
        panelBotonesPreguntas.add(btnVolver);

        areaConsolaVisual = new JTextArea(5, 80);
        areaConsolaVisual.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaConsolaVisual);

        panelInferior.add(panelBotonesPreguntas, BorderLayout.NORTH);
        panelInferior.add(scroll, BorderLayout.CENTER);

        panelTableroJuego.add(panelInferior, BorderLayout.SOUTH);
    }

    // PREGUNTA DEL PROFESOR: ¿Dónde está el CASTEO para cambiar de pantalla con CardLayout? -> Acá
    private void cambiarPantalla(String nombreCarta) {
        CardLayout cl = (CardLayout) panelPrincipal.getLayout(); // <- ESTE ES EL CASTEO EXPLÍCITO
        cl.show(panelPrincipal, nombreCarta);
    }

    private void iniciarPartidaVisual() {
        secretoHumano = null;
        comodinUsado = false;
        secretoMaquina = todosLosPersonajes.get(new Random().nextInt(todosLosPersonajes.size()));
        sospechososDelHumano = new ArrayList<>(todosLosPersonajes);
        sospechososDeLaMaquina = new ArrayList<>(todosLosPersonajes);
        preguntasHechasPorMaquina = new ArrayList<>();

        // Volvemos a hacer visibles las 23 tarjetas al empezar una partida nueva
        for (JButton btn : botonesTarjetas) {
            btn.setVisible(true);
        }

        etiquetaEstado.setText("PASO 1: Haz clic en una tarjeta para elegir tu PERSONAJE SECRETO.");
        areaConsolaVisual.setText("Partida iniciada. Elige tu personaje haciendo clic sobre él.\n");
        cambiarPantalla("PANTALLA_JUEGO");
    }

    // Sirve primero para elegir tu personaje secreto, y después para ARRIESGAR quién tiene la máquina
    private void alHacerClicEnTarjeta(Personaje p) {
        if (secretoHumano == null) {
            // Búsqueda Binaria (D&C) para validar y asignar el personaje secreto del jugador
            secretoHumano = motorIA.buscarPorIdBinaria(todosLosPersonajes, p.getId());
            etiquetaEstado.setText("Tu secreto es: " + secretoHumano.getNombre()
                    + " | Ahora usa los botones de abajo para preguntar o toca una tarjeta para ARRIESGAR.");
            areaConsolaVisual.append("Elegiste en secreto a " + secretoHumano.getNombre() + ". ¡Tu turno de preguntar!\n");
            return;
        }

        // Si ya tenías personaje secreto, hacer clic en una tarjeta significa ARRIESGAR
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Quieres ARRIESGAR que el personaje de la máquina es " + p.getNombre() + "?",
                "Arriesgar Suposición", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            if (p.getId() == secretoMaquina.getId()) {
                JOptionPane.showMessageDialog(this, "¡¡GANASTE!! El personaje de la máquina era " + secretoMaquina.getNombre());
                cambiarPantalla("PANTALLA_MENU");
            } else {
                JOptionPane.showMessageDialog(this, "Incorrecto, no es " + p.getNombre() + ". Se descarta la tarjeta y juega la máquina.");
                actualizarTarjetasVisibles();
                ejecutarTurnoMaquina();
            }
        }
    }

    private void hacerPreguntaAlOponente(String filtro) {
        if (secretoHumano == null) {
            JOptionPane.showMessageDialog(this, "Primero haz clic en una tarjeta para elegir tu personaje secreto.");
            return;
        }

        boolean cumple = motorIA.cumpleFiltro(secretoMaquina, filtro);
        areaConsolaVisual.append("Tú preguntaste: " + motorIA.formatearPreguntaLegible(filtro)
                + " -> Máquina responde: " + (cumple ? "SÍ" : "NO") + "\n");

        // Filtramos la lista y ocultamos con setVisible(false) los descartados
        sospechososDelHumano = motorIA.filtrarSospechosos(sospechososDelHumano, filtro, cumple);
        actualizarTarjetasVisibles();

        ejecutarTurnoMaquina();
    }

    private boolean comodinUsado = false;

    private void usarComodinDivideYConquista() {
        if (secretoHumano == null) {
            JOptionPane.showMessageDialog(this, "Primero haz clic en una tarjeta para elegir tu personaje secreto.");
            return;
        }
        if (comodinUsado) {
            JOptionPane.showMessageDialog(this, "Ya utilizaste tu comodín Divide y Conquista en esta partida.");
            return;
        }
        if (sospechososDelHumano.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Solo te queda 1 sospechoso en el tablero, ¡haz clic en su tarjeta para arriesgar!");
            return;
        }

        int cantidadAntes = sospechososDelHumano.size();
        // Aplicamos Divide y Conquista sobre los sospechosos QUE QUEDAN
        sospechososDelHumano = motorIA.darPistaDivideYConquista(sospechososDelHumano, secretoMaquina);
        comodinUsado = true;

        int primerId = sospechososDelHumano.get(0).getId();
        int ultimoId = sospechososDelHumano.get(sospechososDelHumano.size() - 1).getId();

        areaConsolaVisual.append("[Comodín D&C] Tablero reducido de " + cantidadAntes + " a "
                + sospechososDelHumano.size() + " sospechosos (quedan entre ID #" + primerId + " y #" + ultimoId + ").\n");

        JOptionPane.showMessageDialog(this,
                "[Comodín Divide y Conquista]\n" +
                        "Se dividieron tus " + cantidadAntes + " sospechosos restantes a la mitad.\n" +
                        "El personaje secreto está en la mitad que va del ID #" + primerId + " al #" + ultimoId + ".\n" +
                        "¡La otra mitad fue descartada del tablero!");

        actualizarTarjetasVisibles();
        ejecutarTurnoMaquina();
    }

    // PREGUNTA DEL PROFESOR: ¿Cómo desaparecen las tarjetas descartadas? -> Con setVisible(false)
    private void actualizarTarjetasVisibles() {
        for (int i = 0; i < todosLosPersonajes.size(); i++) {
            Personaje p = todosLosPersonajes.get(i);
            boolean sigueSiendoSospechoso = sospechososDelHumano.contains(p);
            botonesTarjetas[i].setVisible(sigueSiendoSospechoso);
        }
    }

    private void ejecutarTurnoMaquina() {
        if (sospechososDeLaMaquina.size() == 1) {
            Personaje elegido = sospechososDeLaMaquina.get(0);
            JOptionPane.showMessageDialog(this,
                    "¡A la máquina solo le queda 1 sospechoso!\nArriesga: " + elegido.getNombre() + " y ¡GANA LA PARTIDA!");
            cambiarPantalla("PANTALLA_MENU");
            return;
        }

        // La máquina piensa usando el algoritmo Greedy (y lo imprime en la consola de abajo)
        String mejorFiltro = motorIA.seleccionarPreguntaGreedy(sospechososDeLaMaquina, preguntasHechasPorMaquina, true);
        if (mejorFiltro != null) {
            preguntasHechasPorMaquina.add(mejorFiltro);
            boolean respuesta = motorIA.cumpleFiltro(secretoHumano, mejorFiltro);
            sospechososDeLaMaquina = motorIA.filtrarSospechosos(sospechososDeLaMaquina, mejorFiltro, respuesta);

            areaConsolaVisual.append("[Turno IA Greedy] Pregunta: " + motorIA.formatearPreguntaLegible(mejorFiltro)
                    + " (Resp: " + (respuesta ? "SÍ" : "NO") + ") -> Le quedan "
                    + sospechososDeLaMaquina.size() + " sospechosos.\n");
        }
    }
}