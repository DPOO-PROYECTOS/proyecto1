package InterfazGraficas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import logica.CafeLogica;
import modelo.Venta;
import modelo.VentaCafeteria;
import modelo.VentaJuego;

public class PanelBarras extends JPanel {

	private CafeLogica logica;
	private ChartPanel chartPanel;
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");

	public PanelBarras(CafeLogica logica) {
		this.logica = logica;
		setLayout(new BorderLayout());

		JPanel norte = new JPanel();
		JLabel lbl = new JLabel("Fecha inicio (d/M/yyyy):");
		JTextField txtFecha = new JTextField(LocalDate.now().format(fmt), 10);
		JButton btnActualizar = new JButton("Actualizar");

		norte.add(lbl);
		norte.add(txtFecha);
		norte.add(btnActualizar);
		add(norte, BorderLayout.NORTH);

		chartPanel = new ChartPanel(crearChart(LocalDate.now()));
		add(chartPanel, BorderLayout.CENTER);

		btnActualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate inicio = LocalDate.parse(txtFecha.getText(), fmt);
					chartPanel.setChart(crearChart(inicio));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Fecha inválida. Usa d/M/yyyy");
				}
			}
		});
	}

	private JFreeChart crearChart(LocalDate inicio) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < 5; i++) {
			LocalDate dia = inicio.plusDays(i);
			double totalCafe = 0;
			double totalJuegos = 0;
			for (Venta v : logica.getCafe().getVentas()) {
				if (!v.getFecha().toLocalDate().equals(dia)) continue;
				if (v instanceof VentaCafeteria) totalCafe += v.calcularSubtotal();
				else if (v instanceof VentaJuego) totalJuegos += v.calcularSubtotal();
			}
			String etiqueta = dia.format(fmt);
			dataset.addValue(totalCafe, "Cafetería", etiqueta);
			dataset.addValue(totalJuegos, "Juegos", etiqueta);
		}

		return ChartFactory.createBarChart(
			"Ventas por periodo (" + inicio.format(fmt) + ")",
			"Fecha",
			"Valor en Pesos",
			dataset
		);
	}
}
