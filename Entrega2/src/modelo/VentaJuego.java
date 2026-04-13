package modelo;

import java.time.LocalDateTime;

public class VentaJuego extends Venta {

    // Attributes
    private static final double IVA = 0.19;



    // Constructor
    public VentaJuego(LocalDateTime fecha, Usuario usuario) {
        super(fecha, usuario);
    }



    // Methods
    public double getIva() { return IVA; }


    @Override
    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        double descuento = calcularDescuento(subtotal);
        return (subtotal - descuento) * (1 + IVA);
    }


    public double calcularDescuento(double subtotal) {
        Empleado emp = getEmpleadoDescuento();
        if (emp == null) return 0;
        if (getUsuario() instanceof Empleado) return subtotal * 0.20;
        return subtotal * 0.10;
    }
}
