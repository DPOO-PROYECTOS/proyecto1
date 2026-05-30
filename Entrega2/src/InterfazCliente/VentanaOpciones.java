package InterfazCliente;

import javax.swing.*;

public class VentanaOpciones extends JFrame{

	private VentanaCliente ventanaCliente;

	public VentanaOpciones(VentanaCliente ventana) {
		this.ventanaCliente = ventana;
		setTitle("Opciones Cliente");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		PanelOpcionesCliente panel = new PanelOpcionesCliente(ventanaCliente);
		add(panel);
	}
}
