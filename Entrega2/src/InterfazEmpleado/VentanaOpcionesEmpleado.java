package InterfazEmpleado;

import javax.swing.*;

public class VentanaOpcionesEmpleado extends JFrame {

	private VentanaEmpleado ventanaEmpleado;

	public VentanaOpcionesEmpleado(VentanaEmpleado ventana) {
		this.ventanaEmpleado = ventana;
		setTitle("Opciones Empleado");
		setSize(600, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		PanelOpcionesEmpleado panel = new PanelOpcionesEmpleado(ventana);
		add(panel);
	}
}
