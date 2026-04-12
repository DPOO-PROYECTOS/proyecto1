package modelo;

public class SugerenciaPlatillo {

    // Attributes
    private String descripcion;
    private ItemMenu itemSugerido;
    private boolean aprobada;
    private Empleado empleado;
    private Admin admin;



    // Constructor
    public SugerenciaPlatillo(String descripcion, ItemMenu itemSugerido, Empleado empleado) {
        this.descripcion = descripcion;
        this.itemSugerido = itemSugerido;
        this.empleado = empleado;
        this.aprobada = false;
    }



    // ===== MÉTODOS =====
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public ItemMenu getItemSugerido() { return itemSugerido; }
    public void setItemSugerido(ItemMenu itemSugerido) { this.itemSugerido = itemSugerido; }

    public boolean isAprobada() { return aprobada; }
    public void setAprobada(boolean aprobada) { this.aprobada = aprobada; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
}
