package InterfazCliente;

import java.awt.*;
import javax.swing.*;
import logica.CafeLogica;
import modelo.Cafe;
import modelo.Cliente;
import persistencia.CentralPersistencia;

public class VentanaCliente extends JFrame{

	private CafeLogica logica;
	private VentanaOpciones ventanaOpciones;
	private Cliente clienteLogueado;

	public VentanaCliente(CafeLogica logica) {
		this.logica = logica;
		setTitle("Modulo Clientes");
		setSize(500,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		PanelClienteLogin panelLogin = new PanelClienteLogin(logica, this);
		add(panelLogin);
	}

	public void setClienteLogueado(Cliente cliente) {
		this.clienteLogueado = cliente;
	}

	public Cliente getClienteLogueado() {
		return clienteLogueado;
	}

	public CafeLogica getLogica() {
		return logica;
	}

	public void mostrarVentanaOpciones() {
		if (ventanaOpciones == null || !ventanaOpciones.isVisible()) {
			ventanaOpciones = new VentanaOpciones(this);
			ventanaOpciones.setVisible(true);

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
		VentanaCliente ventanaCliente = new VentanaCliente(logica);
		ventanaCliente.setVisible(true);
	}
}