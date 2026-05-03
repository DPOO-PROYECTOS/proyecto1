package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class EmpleadosTurnosTest {
	private Cafe cafe;
	private CafeLogica logica;
	private Admin admin;
	private Mesero mesero1;
	private Mesero mesero2;
	private Empleado cocinero;
	private Turno turnoMesero1;
	@BeforeEach
	public void setUp() {
		cafe= new Cafe("Uniandes Board", 100);
		logica= new CafeLogica(cafe);
		
		admin= logica.registrarAdmin("admin", "1234");
		mesero1= (Mesero) logica.registrarEmpleado("m1", "pass", "mesero");
		mesero2= (Mesero) logica.registrarEmpleado("m2", "pass", "mesero");
		
		cocinero= logica.registrarEmpleado("c1", "pass", "cocinero");
		
		turnoMesero1= logica.crearTurno(mesero1, "Lunes", "08:00", "16:00");
		logica.crearTurno(mesero2, "Lunes", "08:00", "16:00");
		logica.crearTurno(cocinero, "Lunes", "08:00", "16:00");
				
	}
	@Test 
	public void testEliminarTurnoPorReglaMinimos() {
		
		assertThrows(IllegalStateException.class, ()-> {
			logica.eliminarTurno(turnoMesero1);
		}, "Regla: obligatoriamente 2 meseros y 1 cocinero");
		
		assertEquals(3, cafe.getPlanSemanal().getTurnosDia("Lunes").size(), "Agregamos 3, debe haber 3");
		
	}
	
}
