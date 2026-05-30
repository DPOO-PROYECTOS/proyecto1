package InterfazAdmin;

import javax.swing.*;

public class VentanaMenuAdmin extends JFrame {

	public VentanaMenuAdmin(VentanaAdmin ventanaAdmin) {
		setTitle("Menú Admin — Uniandes Board");
		setSize(700, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		PanelMenuAdmin panel = new PanelMenuAdmin(ventanaAdmin);
		add(panel);
	}
}
