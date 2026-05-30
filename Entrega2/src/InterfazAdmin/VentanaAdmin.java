package InterfazAdmin;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Admin;
import modelo.Cafe;
import persistencia.CentralPersistencia;

public class VentanaAdmin extends JFrame {

	private CafeLogica logica;
	private Admin adminLogueado;
	private VentanaMenuAdmin ventanaMenu;

	public VentanaAdmin(CafeLogica logica) {
		this.logica = logica;
		setTitle("Módulo Administración");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		PanelAdminLogin panelLogin = new PanelAdminLogin(logica, this);
		add(panelLogin);
	}

	public void setAdminLogueado(Admin admin) {
		this.adminLogueado = admin;
	}

	public Admin getAdminLogueado() {
		return adminLogueado;
	}

	public CafeLogica getLogica() {
		return logica;
	}

	public void mostrarMenuAdmin() {
		if (ventanaMenu == null || !ventanaMenu.isVisible()) {
			ventanaMenu = new VentanaMenuAdmin(this);
			ventanaMenu.setVisible(true);
		}
	}

	public static void main(String[] args) {
		Cafe cafe = new Cafe("Uniandes Board", 100);
		CafeLogica logica = new CafeLogica(cafe);
		try {
			CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
		} catch (Exception e) {
			System.out.println("No se pudieron cargar los datos, iniciando en blanco");
		}
		// si no hay admin se crea uno por defecto, para no quedar bloqueados
		if (cafe.buscarUsuarioPorLogin("admin") == null) {
			logica.registrarAdmin("admin", "1234");
		}
		VentanaAdmin ventana = new VentanaAdmin(logica);
		ventana.setVisible(true);
	}
}
