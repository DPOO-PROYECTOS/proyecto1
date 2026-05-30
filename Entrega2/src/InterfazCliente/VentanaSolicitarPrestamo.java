package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.JuegoDeMesa;

public class VentanaSolicitarPrestamo extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaSolicitarPrestamo(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Solicitar Préstamo de Juego");
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lbl = new JLabel("Juego disponible:");
		DefaultComboBoxModel<JuegoDeMesa> mod = new DefaultComboBoxModel<>();
		for (JuegoDeMesa j : ventanaCliente.getLogica().getInventarioPrestamoDisponible()) {
			mod.addElement(j);
		}
		JComboBox<JuegoDeMesa> cmb = new JComboBox<>(mod);

		JButton btnSolicitar = new JButton("Solicitar");
		JButton btnCancelar = new JButton("Cancelar");

		btnSolicitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = (JuegoDeMesa) cmb.getSelectedItem();
				if (j == null) {
					JOptionPane.showMessageDialog(null, "No hay juegos disponibles");
					return;
				}
				try {
					ventanaCliente.getLogica().solicitarPrestamoCliente(ventanaCliente.getClienteLogueado(), j.getNombre());
					JOptionPane.showMessageDialog(null, "Préstamo solicitado: " + j.getNombre());
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

		panel.add(lbl);
		panel.add(cmb);
		panel.add(btnSolicitar);
		panel.add(btnCancelar);
		add(panel);
	}
}
