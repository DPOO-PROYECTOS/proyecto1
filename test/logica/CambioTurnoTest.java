package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CambioTurnoTest {

    private Cafe cafe;
    private CafeLogica logica;
    private Admin admin;
    private Empleado mesero1;
    private Empleado mesero2;
    private Turno turnoMesero1;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
        admin   = logica.registrarAdmin("admin", "1234");
        mesero1 = logica.registrarEmpleado("juan", "1234", "mesero");
        mesero2 = logica.registrarEmpleado("pedro", "1234", "mesero");
        Empleado cocinero = logica.registrarEmpleado("chef", "1234", "cocinero");

        // Plan mínimo viable: 2 meseros + 1 cocinero el Lunes
        turnoMesero1 = logica.crearTurno(mesero1,  "Lunes", "08:00", "16:00");
                       logica.crearTurno(mesero2,  "Lunes", "08:00", "16:00");
                       logica.crearTurno(cocinero, "Lunes", "08:00", "16:00");
    }

    // Solicitar cambio de turno crea una solicitud en estado pendiente
    @Test
    public void testSolicitarCambioCreaSolicitudPendiente() {
        logica.solicitarCambioTurno(mesero1, turnoMesero1, mesero2);

        assertEquals(1, logica.getSolicitudesPendientes().size(),
                "Debe haber exactamente 1 solicitud pendiente");
        assertEquals("pendiente", logica.getSolicitudesPendientes().get(0).getEstado());
    }

    // Admin aprueba el cambio y los turnos quedan intercambiados
    @Test
    public void testAprobarCambioIntercambiaEmpleados() {
        SolicitudCambioTurno solicitud = logica.solicitarCambioTurno(mesero1, turnoMesero1, mesero2);

        // Buscar el turno de mesero2 ese día antes de aprobar
        Turno turnoMesero2 = null;
        for (Turno t : cafe.getPlanSemanal().getTurnosDia("Lunes")) {
            if (t.getEmpleado().equals(mesero2)) {
                turnoMesero2 = t;
                break;
            }
        }

        logica.aprobarCambioTurno(admin, solicitud);

        // Tras aprobar: turnoMesero1 debe tener a mesero2, y turnoMesero2 a mesero1
        assertEquals(mesero2, turnoMesero1.getEmpleado(),
                "El turno original de mesero1 debe quedar asignado a mesero2");
        assertNotNull(turnoMesero2);
        assertEquals(mesero1, turnoMesero2.getEmpleado(),
                "El turno original de mesero2 debe quedar asignado a mesero1");
        assertEquals("aprobada", solicitud.getEstado());
    }
}
