package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import logica.CafeLogica;
import modelo.Admin;
import modelo.SolicitudCambioTurno;

public class VentanaCambiosTurno extends JFrame {

	private CafeLogica logica;
	private Admin admin;
	private DefaultTableModel tableModel;
	private List<SolicitudCambioTurno> solicitudes;

	public VentanaCambiosTurno(CafeLogica logica, Admin admin) {
		this.logica = logica;
		this.admin = admin;
		setTitle("Solicitudes de Cambio de Turno");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String[] cols = { "Solicitante", "Intercambiar con", "Día", "Horario" };
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
				if (idx < 0) { JOptionPane.showMessageDialog(null, "Selecciona una solicitud"); return; }
				try {
					logica.aprobarCambioTurno(admin, solicitudes.get(idx));
					recargarTabla();
					JOptionPane.showMessageDialog(null, "Solicitud aprobada");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnRechazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = tabla.getSelectedRow();
				if (idx < 0) { JOptionPane.showMessageDialog(null, "Selecciona una solicitud"); return; }
				try {
					logica.rechazarCambioTurno(admin, solicitudes.get(idx));
					recargarTabla();
					JOptionPane.showMessageDialog(null, "Solicitud rechazada");
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
		solicitudes = logica.getSolicitudesPendientes();
		for (SolicitudCambioTurno s : solicitudes) {
			Object[] fila = {
				s.getSolicitante().getLogin(),
				s.getIntercambiarCon().getLogin(),
				s.getTurno().getDia(),
				s.getTurno().getHoraInicio() + " - " + s.getTurno().getHoraFin()
			};
			tableModel.addRow(fila);
		}
	}
}
