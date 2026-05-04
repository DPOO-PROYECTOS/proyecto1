package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class VentaCafeteriaTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Cliente cliente;
    private Mesa mesa;
    private Bebida cafe5000;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
        cliente = logica.registrarCliente("ana", "1234");

        mesa = new Mesa(1, 4);
        logica.agregarMesa(mesa);
        logica.asignarMesa(cliente, 2, false, false);

        cafe5000 = new Bebida("Cafe", 5000.0, false, false);
        logica.agregarItemMenu(cafe5000);
    }

    // Impuesto de consumo 8% se aplica correctamente y propina sugerida es 10% del subtotal
    @Test
    public void testImpuestoYPropinaSugerida() {
        VentaCafeteria venta = logica.realizarPedidoCafe(cliente, mesa, List.of(cafe5000), -1, 0, false);

        double subtotalEsperado = 5000.0;
        double propinaEsperada  = subtotalEsperado * 0.10;   // 500
        double totalEsperado    = subtotalEsperado * 1.08 + propinaEsperada; // 5400 + 500 = 5900

        assertEquals(subtotalEsperado, venta.calcularSubtotal(), 0.01);
        assertEquals(propinaEsperada,  venta.getPropina(),        0.01);
        assertEquals(totalEsperado,    venta.calcularTotal(),     0.01);
    }
}
