package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MesaTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Cliente cliente;
    private Mesa mesa;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 20);
        logica = new CafeLogica(cafe);
        cliente = logica.registrarCliente("juan", "1234");
        mesa = new Mesa(1, 6);
        logica.agregarMesa(mesa);
    }

    // Asignar una mesa disponible la deja ocupada y asociada al cliente
    @Test
    public void testAsignarMesaExitosa() {
        Mesa asignada = logica.asignarMesa(cliente, 4, false, false);

        assertFalse(asignada.estaDisponible(), "La mesa debe quedar ocupada");
        assertEquals(cliente, asignada.getClienteAsignado(), "El cliente asignado debe coincidir");
        assertEquals(4, asignada.getNumPersonas(), "Debe registrar el número de personas");
    }

    // Liberar la mesa la devuelve al estado disponible
    @Test
    public void testLiberarMesaQuedaDisponible() {
        logica.asignarMesa(cliente, 3, false, false);
        assertFalse(mesa.estaDisponible());

        logica.liberarMesa(mesa);

        assertTrue(mesa.estaDisponible(), "La mesa debe quedar disponible tras liberarla");
        assertNull(mesa.getClienteAsignado(), "No debe haber cliente asignado");
    }

    // Pedir más personas de las que soporta el café lanza excepción
    @Test
    public void testCapacidadMaximaCafeExcedida() {
        // El Cafe tiene capacidad 20 pedimos 21 personas
        assertThrows(IllegalStateException.class, () -> {
            logica.asignarMesa(cliente, 21, false, false);
        }, "Debe lanzar excepción al superar la capacidad máxima del café");
    }
}
