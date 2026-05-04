package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrestamoTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Cliente cliente;
    private JuegoDeMesa juegoTablero;
    private JuegoDeMesa juegoAccion;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
        cliente = logica.registrarCliente("ana", "1234");

        juegoTablero = new JuegoTablero("Catan", 1995, "Devir", 2, 4, false, false, "Bueno", true);
        juegoAccion = new JuegoDeAccion("Jenga", 1983, "Hasbro", 2, 6, false, false, "Bueno", true);

        logica.agregarJuegoInventarioPrestamo(juegoTablero);
        logica.agregarJuegoInventarioPrestamo(juegoAccion);

        // El cliente necesita mesa para poder pedir préstamo
        Mesa mesa = new Mesa(1, 6);
        logica.agregarMesa(mesa);
        logica.asignarMesa(cliente, 3, false, false);
    }

    // Préstamo exitoso incrementa el contador de vecesPrestado
    @Test
    public void testPrestamoIncrementaVecesPrestado() {
        assertEquals(0, juegoTablero.getVecesPrestado(), "Debe empezar en 0");

        logica.solicitarPrestamoCliente(cliente, "Catan");

        assertEquals(1, juegoTablero.getVecesPrestado(), "Debe haber subido a 1 tras el préstamo");
        assertFalse(juegoTablero.isDisponible(), "El juego debe quedar no disponible");
    }

    // Devolver un juego lo vuelve disponible
    @Test
    public void testDevolverJuegoQuedaDisponible() {
        PrestamoCliente prestamo = logica.solicitarPrestamoCliente(cliente, "Catan");
        assertFalse(juegoTablero.isDisponible());

        logica.devolverJuego(prestamo, juegoTablero);

        assertTrue(juegoTablero.isDisponible(), "El juego debe quedar disponible tras la devolución");
        assertFalse(prestamo.getJuegos().contains(juegoTablero), "El juego ya no debe estar en el préstamo");
    }

    // Pedir un tercer juego cuando ya se tienen 2 lanza excepción
    @Test
    public void testLimiteDosJuegosPorCliente() {
        JuegoDeMesa tercer = new JuegoTablero("Pandemic", 2008, "Z-Man", 2, 4, false, false, "Bueno", true);
        logica.agregarJuegoInventarioPrestamo(tercer);

        logica.solicitarPrestamoCliente(cliente, "Catan");
        logica.solicitarPrestamoCliente(cliente, "Jenga");

        assertThrows(IllegalStateException.class, () -> {
            logica.solicitarPrestamoCliente(cliente, "Pandemic");
        }, "No se pueden tener más de 2 juegos en préstamo simultáneamente");
    }
}
