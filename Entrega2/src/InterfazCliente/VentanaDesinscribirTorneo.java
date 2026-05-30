package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.Torneo;

public class VentanaDesinscribirTorneo extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaDesinscribirTorneo(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Desinscribirse de Torneo");
		setSize(420, 180);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblTorneo = new JLabel("Torneo en el que estás inscrito:");
		DefaultComboBoxModel<Torneo> mod = new DefaultComboBoxModel<>();
		for (Torneo t : ventanaCliente.getLogica().getCafe().getTorneos()) {
			if (t.getInscripciones().containsKey(ventanaCliente.getClienteLogueado())) {
				mod.addElement(t);
			}
		}
		JComboBox<Torneo> cmb = new JComboBox<>(mod);

		JButton btnDesinscribir = new JButton("Desinscribirse");
		JButton btnCancelar = new JButton("Cancelar");

		btnDesinscribir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Torneo t = (Torneo) cmb.getSelectedItem();
				if (t == null) {
					JOptionPane.showMessageDialog(null, "No estás inscrito en ningún torneo");
					return;
				}
				try {
					ventanaCliente.getLogica().desinscribirTorneo(ventanaCliente.getClienteLogueado(), t);
					JOptionPane.showMessageDialog(null, "Desinscripción exitosa");
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
		panel.add(btnDesinscribir);
		panel.add(btnCancelar);
		add(panel);
	}
}
