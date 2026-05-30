package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.JuegoDeMesa;

public class VentanaMoverVentaPrestamo extends JFrame {

	private CafeLogica logica;

	public VentanaMoverVentaPrestamo(CafeLogica logica) {
		this.logica = logica;
		setTitle("Mover de Venta a Préstamo");
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lbl = new JLabel("Juego a mover:");
		DefaultComboBoxModel<JuegoDeMesa> modelo = new DefaultComboBoxModel<>();
		for (JuegoDeMesa j : logica.getInventarioVenta()) {
			modelo.addElement(j);
		}
		JComboBox<JuegoDeMesa> cmbJuego = new JComboBox<>(modelo);

		JButton btnMover = new JButton("Mover");
		JButton btnCancelar = new JButton("Cancelar");

		btnMover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa juego = (JuegoDeMesa) cmbJuego.getSelectedItem();
				if (juego == null) {
					JOptionPane.showMessageDialog(null, "No hay juegos en venta");
					return;
				}
				try {
					logica.moverDeVentaAPrestamo(juego);
					JOptionPane.showMessageDialog(null, juego.getNombre() + " movido a préstamo");
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
		panel.add(cmbJuego);
		panel.add(btnMover);
		panel.add(btnCancelar);
		add(panel);
	}
}
