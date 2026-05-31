package InterfazEmpleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.Empleado;
import modelo.Turno;
import modelo.Usuario;

public class VentanaSolicitarCambioTurno extends JFrame {

	private VentanaEmpleado ventanaEmpleado;

	public VentanaSolicitarCambioTurno(VentanaEmpleado ventanaEmpleado) {
		this.ventanaEmpleado = ventanaEmpleado;
		setTitle("Solicitar Cambio de Turno");
		setSize(500, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblTurno = new JLabel("Mi turno:");
		DefaultComboBoxModel<Turno> modTurnos = new DefaultComboBoxModel<>();
		for (Turno t : ventanaEmpleado.getLogica().getTurnosDeEmpleado(ventanaEmpleado.getEmpleadoLogueado())) {
			modTurnos.addElement(t);
		}
		JComboBox<Turno> cmbTurno = new JComboBox<>(modTurnos);

		JLabel lblComp = new JLabel("Intercambiar con:");
		DefaultComboBoxModel<Empleado> modComp = new DefaultComboBoxModel<>();
		for (Usuario u : ventanaEmpleado.getLogica().getCafe().getUsuarios()) {
			if (u instanceof Empleado && u != ventanaEmpleado.getEmpleadoLogueado()) {
				modComp.addElement((Empleado) u);
			}
		}
		JComboBox<Empleado> cmbComp = new JComboBox<>(modComp);

		JButton btnSolicitar = new JButton("Solicitar");
		JButton btnCancelar = new JButton("Cancelar");

		btnSolicitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Turno t = (Turno) cmbTurno.getSelectedItem();
				Empleado comp = (Empleado) cmbComp.getSelectedItem();
				if (t == null || comp == null) {
					JOptionPane.showMessageDialog(null, "Selecciona turno y compañero");
					return;
				}
				try {
					ventanaEmpleado.getLogica().solicitarCambioTurno(ventanaEmpleado.getEmpleadoLogueado(), t, comp);
					JOptionPane.showMessageDialog(null, "Solicitud enviada");
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lblTurno);
		panel.add(cmbTurno);
		panel.add(lblComp);
		panel.add(cmbComp);
		panel.add(btnSolicitar);
		panel.add(btnCancelar);
		add(panel);
	}
}
