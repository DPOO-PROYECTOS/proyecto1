package InterfazGraficas;

import javax.swing.*;

import logica.CafeLogica;

public class VentanaGraficas extends JFrame {

	private CafeLogica logica;

	public VentanaGraficas(CafeLogica logica) {
		this.logica = logica;
		setTitle("Gráficas Informes");
		setSize(850, 650);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		// TODO: agregar JTabbedPane con PanelPastel, PanelBarras, PanelLineas
		// pendiente: jfreechart en lib/
		JLabel lblTemp = new JLabel("Módulo de gráficas en construcción", SwingConstants.CENTER);
		add(lblTemp);
	}
}
