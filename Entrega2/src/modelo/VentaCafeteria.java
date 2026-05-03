package modelo;

import java.time.LocalDateTime;

public class VentaCafeteria extends Venta {

    // Attributes
    private static final double IMPUESTO_CONSUMO = 0.08;
    private double propina;



    // Constructor
    public VentaCafeteria(LocalDateTime fecha, Usuario usuario) {
        super(fecha, usuario);
        this.propina = 0;
    }



    // Methods
    public double getPropina() { return propina; }
    public void setPropina(double propina) { this.propina = propina; }


    public double calcularPropinaSugerida() {
        return calcularSubtotal() * 0.10;
    }


    public double getImpuestoConsumo() {
        return calcularSubtotal() * IMPUESTO_CONSUMO;
    }


    public double calcularDescuento() {
        if (getEmpleadoDescuento() != null && getUsuario() instanceof Empleado) {
            return calcularSubtotal() * 0.20;
        }
        return 0;
    }


    @Override
    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        double totalSinPropina= (subtotal - calcularDescuento()) * (1 + IMPUESTO_CONSUMO) + propina;
        
        if (this.descuentoTorneoAplicado) {
        	totalSinPropina *=0.67;
        }
        return totalSinPropina+propina;
    }
}
