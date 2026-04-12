package modelo;

public class LineaVenta {

    // Attributes
    private int cantidad;
    private double precioUnitario;
    private JuegoDeMesa juego;
    private ItemMenu item;



    // Constructor
    public LineaVenta(int cantidad, double precioUnitario, JuegoDeMesa juego) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.juego = juego;
        this.item = null;
    }

    public LineaVenta(int cantidad, double precioUnitario, ItemMenu item) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.item = item;
        this.juego = null;
    }



    // ===== MÉTODOS =====
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public JuegoDeMesa getJuego() { return juego; }
    public void setJuego(JuegoDeMesa juego) { this.juego = juego; }

    public ItemMenu getItem() { return item; }
    public void setItem(ItemMenu item) { this.item = item; }


    public boolean esDeJuego() { return juego != null; }


    public boolean esDeMenu() { return item != null; }


    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
}
