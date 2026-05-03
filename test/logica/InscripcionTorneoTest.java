package logica;
import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InscripcionTorneoTest {
	private Cafe cafe;
	private CafeLogica logica;
	private Admin admin;
	private Cliente cliente1;
	private Cliente clienteFanatico;
	private JuegoDeMesa juegoCatan;
	
	//AAA
	@BeforeEach
	public void setUp() {
		cafe= new Cafe("Uniandes Game", 100);
		logica= new CafeLogica(cafe);
		
		admin=logica.registrarAdmin("Corzo", "1234");
		cliente1= logica.registrarCliente("arce", "abcd");
		clienteFanatico= logica.registrarCliente("angarita", "1234");
		
		juegoCatan= new JuegoTablero("Catan", 1995,"Devir", 3, 4, false, false, "Bueno", true);
		
		logica.agregarJuegoInventarioVenta(juegoCatan, 100000.0);
		clienteFanatico.agregarFavorito(juegoCatan);
		
	}
	
	@Test
	public void testInscripcionExistosaYReglaFanaticos() {
		logica.crearTorneo(admin, juegoCatan, "Lunes", 10, "Amistoso");
		
		Torneo torneo= cafe.getTorneos().get(0);
		
		logica.inscribirEnTorneo(cliente1, torneo, 2);
		logica.inscribirEnTorneo(clienteFanatico, torneo, 1);
		
		
		assertEquals(2, torneo.getInscripciones().get(cliente1), "El cliente normal deberia tener 2 cupos");
		assertEquals(1, torneo.getInscripciones().get(clienteFanatico), "El fanatico deberia tener 1");
		
		assertEquals(6, torneo.getCuposDisponibles(), "Deberian quedar 6 cupos");
		assertEquals(1, torneo.getCuposFanaticosRestantes(), "Deberia quedar 1 cupo reservado para fanaticos");
			
	}
	
	@Test 
	public void testExcedeCupos() {
		logica.crearTorneo(admin, juegoCatan, "Martes", 10, "Amistoso");
		Torneo torneo= cafe.getTorneos().get(0);
		assertThrows(IllegalArgumentException.class, () ->{
			logica.inscribirEnTorneo(cliente1, torneo, 4);
			
		}, "maximo 3 cupos");
	}
	
		
}
