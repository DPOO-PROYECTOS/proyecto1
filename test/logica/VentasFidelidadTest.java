package logica;
import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class VentasFidelidadTest {
	private Cafe cafe;
	private CafeLogica logica;
	private Cliente cliente1;
	private Empleado emp;
	private String codigoemp;
	private JuegoDeMesa juego;
	private ArrayList<JuegoDeMesa> juegos;
	
	@BeforeEach
	public void setUp() {
		cafe= new Cafe("Uniandes Board", 150);
		logica= new CafeLogica(cafe);
		emp= logica.registrarEmpleado("emp1", "pass", "mesero");
		cliente1= logica.registrarCliente("c1", "pass");
		codigoemp=emp.getCodigoDescuento();
		juego= new JuegoTablero("Catan", 1995,"Devir", 3, 4, false, false, "Bueno", true);
		logica.agregarJuegoInventarioVenta(juego, 100000.0);
	}
	
	@Test 
	public void puntosFidelidadTest() {
		juegos = new ArrayList<>();
		juegos.add(juego);
		VentaJuego venta =logica.venderJuegos(cliente1, juegos, codigoemp, 0, false);
		
		assertEquals(107100.0, venta.calcularTotal(), "Debe ser igual a 107100.0");
		assertEquals(1071.0, cliente1.getPuntosFidelidad(), "Debe tener 1071.0 puntos de fidelidad");
	}
}
