package modelo;

public class Personaje {
    private int id;
    private String nombre;
    private String genero;
    private boolean esCalvo;
    private boolean tieneLentes;
    private String colorPelo;

    public Personaje(int id, String nombre, String genero, boolean esCalvo, boolean tieneLentes, String colorPelo) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.esCalvo = esCalvo;
        this.tieneLentes = tieneLentes;
        this.colorPelo = colorPelo;
    }

    public int getId() {

        return id;
    }
    public void setId(int id) {

        this.id = id;
    }

    public String getNombre() {

        return nombre;
    }
    public String getGenero() {

        return genero;
    }
    public boolean isEsCalvo() {

        return esCalvo;
    }
    public boolean isTieneLentes() {

        return tieneLentes;
    }
    public String getColorPelo() {

        return colorPelo;
    }

    @Override
    public String toString() {
        return String.format("ID: %2d | %-8s | %-6s | Calvo: %-5s | Lentes: %-5s | Pelo: %-8s",
                id, nombre, genero, (esCalvo ? "Sí" : "No"), (tieneLentes ? "Sí" : "No"), colorPelo);
    }
}