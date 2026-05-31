package InterfazGraficas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import logica.CafeLogica;
import modelo.Prestamo;

public class PanelLineas extends JPanel {

	private CafeLogica logica;
	private ChartPanel chartPanel;
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");

	public PanelLineas(CafeLogica logica) {
		this.logica = logica;
		setLayout(new BorderLayout());

		JPanel norte = new JPanel();
		JLabel lbl = new JLabel("Semana (Lunes) d/M/yyyy:");
		JTextField txtFecha = new JTextField(lunesActual().format(fmt), 10);
		JButton btnActualizar = new JButton("Actualizar");

		norte.add(lbl);
		norte.add(txtFecha);
		norte.add(btnActualizar);
		add(norte, BorderLayout.NORTH);

		chartPanel = new ChartPanel(crearChart(lunesActual()));
		add(chartPanel, BorderLayout.CENTER);

		btnActualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate lunes = LocalDate.parse(txtFecha.getText(), fmt);
					chartPanel.setChart(crearChart(lunes));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Fecha inválida. Usa d/M/yyyy");
				}
			}
		});
	}

	private LocalDate lunesActual() {
		LocalDate hoy = LocalDate.now();
		// retrocedo hasta llegar al lunes
		while (hoy.getDayOfWeek() != DayOfWeek.MONDAY) {
			hoy = hoy.minusDays(1);
		}
		return hoy;
	}

	private JFreeChart crearChart(LocalDate lunes) {
		int[] conteos = new int[7];
		LocalDate domingo = lunes.plusDays(6);

		for (Prestamo p : logica.getCafe().getHistorialPrestamos()) {
			if (p.getFechaInicio() == null) continue;
			LocalDate fecha = p.getFechaInicio().toLocalDate();
			if (fecha.isBefore(lunes) || fecha.isAfter(domingo)) continue;
			int idx = fecha.getDayOfWeek().getValue() - 1;
			conteos[idx]++;
		}

		String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < 7; i++) {
			dataset.addValue(conteos[i], "Reservas", dias[i]);
		}

		return ChartFactory.createLineChart(
			"Reservas Semana del " + lunes.format(fmt),
			"Día de la Semana",
			"Número Reservas",
			dataset
		);
	}
}
