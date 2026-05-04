package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RestriccionesPrestamoTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Cliente cliente;
    private Mesa mesa;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
        cliente = logica.registrarCliente("ana", "1234");
    }

    // Juego de Acción no se puede prestar si la mesa tiene una bebida caliente activa
    @Test
    public void testAccionBloqueadaConBebidaCaliente() {
        mesa = new Mesa(1, 4);
        logica.agregarMesa(mesa);
        logica.asignarMesa(cliente, 2, false, false);

        // Primero se sirve una bebida caliente en la mesa
        Bebida teCaliente = new Bebida("Te", 3000.0, false, true);
        logica.agregarItemMenu(teCaliente);
        logica.realizarPedidoCafe(cliente, mesa, List.of(teCaliente), 0, 0, false);

        // Luego se intenta pedir un juego de Acción
        JuegoDeMesa jenga = new JuegoDeAccion("Jenga", 1983, "Hasbro", 2, 6, false, false, "Bueno", true);
        logica.agregarJuegoInventarioPrestamo(jenga);

        assertThrows(IllegalStateException.class, () -> {
            logica.solicitarPrestamoCliente(cliente, "Jenga");
        }, "No se debe poder prestar un juego de Acción a una mesa con bebida caliente");
    }

    // Bebida alcohólica no se puede servir a una mesa con menores de edad
    @Test
    public void testBebidaAlcoholicaRechazadaConMenores() {
        mesa = new Mesa(2, 6);
        logica.agregarMesa(mesa);
        logica.asignarMesa(cliente, 3, true, false); // tieneNinos = true

        Bebida cerveza = new Bebida("Cerveza", 8000.0, true, false);
        logica.agregarItemMenu(cerveza);

        assertThrows(IllegalStateException.class, () -> {
            logica.realizarPedidoCafe(cliente, mesa, List.of(cerveza), 0, 0, false);
        }, "No se debe servir bebida alcohólica a una mesa con menores de 5 años");
    }
}
