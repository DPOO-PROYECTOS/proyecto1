package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Venta {

    // ===== ATRIBUTOS =====
    private LocalDateTime fecha;
    private Usuario usuario;
    private Empleado empleadoDescuento;
    private List<LineaVenta> lineas = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    protected Venta(LocalDateTime fecha, Usuario usuario) {
        this.fecha = fecha;
        this.usuario = usuario;
    }



    // ===== MÉTODOS =====
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Empleado getEmpleadoDescuento() { return empleadoDescuento; }
    public void setEmpleadoDescuento(Empleado empleadoDescuento) { this.empleadoDescuento = empleadoDescuento; }

    public List<LineaVenta> getLineas() { return lineas; }
    public void setLineas(List<LineaVenta> lineas) { this.lineas = lineas; }


    public void agregarLinea(LineaVenta linea) {
        lineas.add(linea);
    }


    public double calcularSubtotal() {
        return lineas.stream()
                .mapToDouble(LineaVenta::calcularSubtotal)
                .sum();
    }


    public abstract double calcularTotal();
}
