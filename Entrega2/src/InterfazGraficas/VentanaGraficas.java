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

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Disponibilidad Juego", new PanelPastel(logica));
		tabs.addTab("Ventas Período", new PanelBarras(logica));
		tabs.addTab("Reservas Semana", new PanelLineas(logica));
		add(tabs);
	}
}
