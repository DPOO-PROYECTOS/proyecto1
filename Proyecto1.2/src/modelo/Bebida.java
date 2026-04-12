package modelo;

public class Bebida extends ItemMenu {

    // Attributes
    private boolean esAlcoholica;
    private boolean esCaliente;



    // Constructor
    public Bebida(String nombre, double precio, boolean esAlcoholica, boolean esCaliente) {
        super(nombre, precio);
        this.esAlcoholica = esAlcoholica;
        this.esCaliente = esCaliente;
    }



    // ===== MÉTODOS =====
    public boolean isEsAlcoholica() { return esAlcoholica; }
    public void setEsAlcoholica(boolean esAlcoholica) { this.esAlcoholica = esAlcoholica; }

    public boolean isEsCaliente() { return esCaliente; }
    public void setEsCaliente(boolean esCaliente) { this.esCaliente = esCaliente; }
}
