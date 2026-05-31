package InterfazGraficas;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import logica.CafeLogica;
import modelo.JuegoDeMesa;

public class PanelPastel extends JPanel {

	private CafeLogica logica;
	private ChartPanel chartPanel;

	public PanelPastel(CafeLogica logica) {
		this.logica = logica;
		setLayout(new BorderLayout());

		JPanel norte = new JPanel();
		JLabel lbl = new JLabel("Juego:");

		// nombres unicos de ambos inventarios
		Set<String> nombres = new HashSet<>();
		for (JuegoDeMesa j : logica.getInventarioVenta()) nombres.add(j.getNombre());
		for (JuegoDeMesa j : logica.getInventarioPrestamo()) nombres.add(j.getNombre());

		DefaultComboBoxModel<String> mod = new DefaultComboBoxModel<>();
		for (String n : nombres) mod.addElement(n);
		JComboBox<String> cmbJuego = new JComboBox<>(mod);

		norte.add(lbl);
		norte.add(cmbJuego);
		add(norte, BorderLayout.NORTH);

		String inicial = cmbJuego.getItemCount() > 0 ? (String) cmbJuego.getSelectedItem() : null;
		chartPanel = new ChartPanel(crearChart(inicial));
		add(chartPanel, BorderLayout.CENTER);

		cmbJuego.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					chartPanel.setChart(crearChart((String) cmbJuego.getSelectedItem()));
				}
			}
		});
	}

	private JFreeChart crearChart(String nombreJuego) {
		int copiasVenta = 0;
		int copiasPrestamo = 0;
		if (nombreJuego != null) {
			for (JuegoDeMesa j : logica.getInventarioVenta()) {
				if (j.getNombre().equals(nombreJuego)) copiasVenta++;
			}
			for (JuegoDeMesa j : logica.getInventarioPrestamo()) {
				if (j.getNombre().equals(nombreJuego)) copiasPrestamo++;
			}
		}

		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		dataset.setValue("Copias Venta", copiasVenta);
		dataset.setValue("Copias Préstamo", copiasPrestamo);

		String titulo = nombreJuego == null ? "Disponibilidad" : "Disponibilidad " + nombreJuego;
		return ChartFactory.createPieChart(titulo, dataset, true, true, false);
	}
}
