package InterfazCliente;

import java.awt.*;
import javax.swing.*;

public class VentanaCliente extends JFrame{
	
	private VentanaOpciones ventanaOpciones;
	
	public VentanaCliente() {
		setTitle("Modulo Clientes");
		setSize(500,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		PanelClienteLogin panelLogin = new PanelClienteLogin();
		add(panelLogin);
	}
	
	public void mostrarVentanaOpciones() {
		if (ventanaOpciones == null || !ventanaOpciones.isVisible()) {
			ventanaOpciones = new VentanaOpciones(this);
			ventanaOpciones.setVisible(true);
			
		}
	}
	
	public static void main(String[] args) {
		VentanaCliente ventanaCliente = new VentanaCliente();
		ventanaCliente.setVisible(true);
	}
}