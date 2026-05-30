package InterfazEmpleado;
import javax.swing.*;
import logica.CafeLogica;
import modelo.Cafe;
import persistencia.CentralPersistencia;

public class VentanaEmpleado extends JFrame{

	private CafeLogica logica;

	public VentanaEmpleado(CafeLogica logica) {
		this.logica = logica;
		setTitle("Módulo de empleados");

		setSize(500,400);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLocationRelativeTo(null);

		setResizable(false);

		PanelEmpleado panelLogin = new PanelEmpleado(logica);
		add(panelLogin);
	}

	public static void main(String[] args) {
		Cafe cafe = new Cafe("Uniandes Board", 100);
		CafeLogica logica = new CafeLogica(cafe);
		try {
			CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
		} catch (Exception e) {
			System.out.println("No se pudieron cargar los datos, iniciando en blanco");
		}
		VentanaEmpleado miVentana= new VentanaEmpleado(logica);
		miVentana.setVisible(true);
	}


}
