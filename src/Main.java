import juego.GestorPartida;
import ui.VentanaJuego;

public class Main {
    public static void main(String[] args) {
        GestorPartida partida = new GestorPartida();
        VentanaJuego ventana = new VentanaJuego(partida);
        ventana.setVisible(true); // Muestra la ventana en pantalla
    }
}