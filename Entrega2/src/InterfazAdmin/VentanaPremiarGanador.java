package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Torneo;
import modelo.Usuario;

public class VentanaPremiarGanador extends JFrame {

	private CafeLogica logica;
	private DefaultComboBoxModel<Usuario> modGanador;

	public VentanaPremiarGanador(CafeLogica logica) {
		this.logica = logica;
		setTitle("Premiar Ganador de Torneo");
		setSize(450, 220);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblTorneo = new JLabel("Torneo:");
		DefaultComboBoxModel<Torneo> modT = new DefaultComboBoxModel<>();
		for (Torneo t : logica.getCafe().getTorneos()) {
			modT.addElement(t);
		}
		JComboBox<Torneo> cmbTorneo = new JComboBox<>(modT);

		JLabel lblGanador = new JLabel("Ganador:");
		modGanador = new DefaultComboBoxModel<>();
		JComboBox<Usuario> cmbGanador = new JComboBox<>(modGanador);

		// inicializamos la lista de ganadores con el primer torneo seleccionado
		actualizarGanadores((Torneo) cmbTorneo.getSelectedItem());

		cmbTorneo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					actualizarGanadores((Torneo) e.getItem());
				}
			}
		});

		JButton btnPremiar = new JButton("Premiar");
		JButton btnCerrar = new JButton("Cerrar");

		btnPremiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Torneo torneo = (Torneo) cmbTorneo.getSelectedItem();
				Usuario ganador = (Usuario) cmbGanador.getSelectedItem();
				if (torneo == null || ganador == null) {
					JOptionPane.showMessageDialog(null, "Selecciona torneo y ganador");
					return;
				}
				try {
					logica.premiarGanador(ganador, torneo);
					JOptionPane.showMessageDialog(null, "Ganador premiado");
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lblTorneo);
		panel.add(cmbTorneo);
		panel.add(lblGanador);
		panel.add(cmbGanador);
		panel.add(btnPremiar);
		panel.add(btnCerrar);
		add(panel);
	}

	private void actualizarGanadores(Torneo torneo) {
		modGanador.removeAllElements();
		if (torneo == null) return;
		for (Usuario u : torneo.getInscripciones().keySet()) {
			modGanador.addElement(u);
		}
	}
}
