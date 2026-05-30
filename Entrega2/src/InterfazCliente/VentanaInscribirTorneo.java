package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.Torneo;

public class VentanaInscribirTorneo extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaInscribirTorneo(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Inscribirse a Torneo");
		setSize(450, 220);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblTorneo = new JLabel("Torneo:");
		DefaultComboBoxModel<Torneo> mod = new DefaultComboBoxModel<>();
		for (Torneo t : ventanaCliente.getLogica().getCafe().getTorneos()) {
			mod.addElement(t);
		}
		JComboBox<Torneo> cmb = new JComboBox<>(mod);

		JLabel lblCupos = new JLabel("Cupos a comprar:");
		JSpinner spnCupos = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

		JButton btnInscribir = new JButton("Inscribirse");
		JButton btnCancelar = new JButton("Cancelar");

		btnInscribir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Torneo t = (Torneo) cmb.getSelectedItem();
				if (t == null) {
					JOptionPane.showMessageDialog(null, "No hay torneos disponibles");
					return;
				}
				int cupos = (Integer) spnCupos.getValue();
				try {
					ventanaCliente.getLogica().inscribirEnTorneo(ventanaCliente.getClienteLogueado(), t, cupos);
					JOptionPane.showMessageDialog(null, "Inscripción exitosa");
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		panel.add(lblTorneo);
		panel.add(cmb);
		panel.add(lblCupos);
		panel.add(spnCupos);
		panel.add(btnInscribir);
		panel.add(btnCancelar);
		add(panel);
	}
}
