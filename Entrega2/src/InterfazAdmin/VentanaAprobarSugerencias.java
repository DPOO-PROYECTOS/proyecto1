package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import logica.CafeLogica;
import modelo.Admin;
import modelo.SugerenciaPlatillo;

public class VentanaAprobarSugerencias extends JFrame {

	private CafeLogica logica;
	private Admin admin;
	private DefaultTableModel tableModel;
	private List<SugerenciaPlatillo> sugerencias;

	public VentanaAprobarSugerencias(CafeLogica logica, Admin admin) {
		this.logica = logica;
		this.admin = admin;
		setTitle("Aprobar Sugerencias de Platillo");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String[] cols = { "Empleado", "Item sugerido", "Descripción" };
		tableModel = new DefaultTableModel(cols, 0);
		recargarTabla();

		JTable tabla = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(tabla);

		JButton btnAprobar = new JButton("Aprobar");
		JButton btnRechazar = new JButton("Rechazar");
		JButton btnCerrar = new JButton("Cerrar");

		btnAprobar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = tabla.getSelectedRow();
				if (idx < 0) { JOptionPane.showMessageDialog(null, "Selecciona una sugerencia"); return; }
				try {
					logica.aprobarSugerencia(admin, sugerencias.get(idx));
					recargarTabla();
					JOptionPane.showMessageDialog(null, "Sugerencia aprobada");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnRechazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = tabla.getSelectedRow();
				if (idx < 0) { JOptionPane.showMessageDialog(null, "Selecciona una sugerencia"); return; }
				try {
					logica.rechazarSugerencia(admin, sugerencias.get(idx));
					recargarTabla();
					JOptionPane.showMessageDialog(null, "Sugerencia rechazada");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		JPanel sur = new JPanel();
		sur.add(btnAprobar);
		sur.add(btnRechazar);
		sur.add(btnCerrar);
		add(sur, BorderLayout.SOUTH);
	}

	private void recargarTabla() {
		tableModel.setRowCount(0);
		sugerencias = logica.getSugerenciasPendientes();
		for (SugerenciaPlatillo s : sugerencias) {
			Object[] fila = {
				s.getEmpleado().getLogin(),
				s.getItemSugerido() != null ? s.getItemSugerido().getNombre() : "(sin item)",
				s.getDescripcion()
			};
			tableModel.addRow(fila);
		}
	}
}
