package Interfaz;
import javax.swing.*;
public class VentanaEmpleado extends JFrame{
	
	public VentanaEmpleado() {
		setTitle("Módulo de empleados");
		
		setSize(500,400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		PanelEmpleado panelLogin = new PanelEmpleado();
		add(panelLogin);
	}
	
	public static void main(String[] args) {
		VentanaEmpleado miVentana= new VentanaEmpleado();
		miVentana.setVisible(true);
	}
	
	
}
