package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TorneosPremiosTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Admin admin;
    private Cliente cliente;
    private Empleado empleado;
    private JuegoDeMesa juego;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
        admin    = logica.registrarAdmin("admin", "1234");
        cliente  = logica.registrarCliente("ana", "1234");
        empleado = logica.registrarEmpleado("emp1", "1234", "mesero");

        juego = new JuegoTablero("Catan", 1995, "Devir", 3, 4, false, false, "Bueno", true);
        logica.agregarJuegoInventarioPrestamo(juego);
    }

    // Premiar ganador de torneo amistoso le otorga el bono de descuento
    @Test
    public void testPremiarGanadorAmistoso() {
        logica.crearTorneo(admin, juego, "Lunes", 10, "Amistoso");
        Torneo torneo = cafe.getTorneos().get(0);
        logica.inscribirEnTorneo(cliente, torneo, 1);

        assertFalse(cliente.getTieneBonoTorneoAmistoso(), "El cliente no debe tener bono antes de ganar");

        logica.premiarGanador(cliente, torneo);

        assertTrue(cliente.getTieneBonoTorneoAmistoso(), "El cliente debe tener el bono tras ganar el torneo amistoso");
    }

    // El empleado ganador de torneo competitivo NO recibe el premio en metálico
    @Test
    public void testEmpleadoNoRecibePremioCompetitivo() {
        logica.crearTorneo(admin, juego, "Martes", 10, "Competitivo");
        TorneoCompetitivo torneo = (TorneoCompetitivo) cafe.getTorneos().get(0);

        // Inscribir al empleado (sin turno ese día → puede inscribirse gratis)
        logica.inscribirEnTorneo(empleado, torneo, 1);

        double premioAntesDePremiacion = torneo.getPremioAcumulado();

        logica.premiarGanador(empleado, torneo);

        // El pozo no debe cambiar: el empleado no cobra
        assertEquals(premioAntesDePremiacion, torneo.getPremioAcumulado(), 0.01,
                "El pozo no debe reducirse al premiar a un empleado");
        assertFalse(empleado.getTieneBonoTorneoAmistoso(),
                "El empleado no debe recibir bono amistoso en torneo competitivo");
    }
}
