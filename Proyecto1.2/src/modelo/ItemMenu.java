package modelo;

public abstract class ItemMenu {

    // ===== ATRIBUTOS =====
    private String nombre;
    private double precio;



    // ===== CONSTRUCTOR =====
    protected ItemMenu(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }



    // ===== MÉTODOS =====
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
