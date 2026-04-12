package modelo;

import java.time.LocalDateTime;

public class PrestamoCliente extends Prestamo {

    // Attributes
    private Cliente cliente;
    private Mesa mesa;



    // Constructor
    public PrestamoCliente(LocalDateTime fechaInicio, Cliente cliente, Mesa mesa) {
        super(fechaInicio);
        this.cliente = cliente;
        this.mesa = mesa;
    }



    // ===== MÉTODOS =====
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
}
