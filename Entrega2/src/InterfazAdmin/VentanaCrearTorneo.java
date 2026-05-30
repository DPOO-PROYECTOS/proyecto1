package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Admin;
import modelo.JuegoDeMesa;

public class VentanaCrearTorneo extends JFrame {

	private CafeLogica logica;
	private Admin admin;

	public VentanaCrearTorneo(CafeLogica logica, Admin admin) {
		this.logica = logica;
		this.admin = admin;
		setTitle("Crear Torneo");
		setSize(420, 320);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblDia = new JLabel("Día semana:");
		String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
		JComboBox<String> cmbDia = new JComboBox<>(dias);

		JLabel lblJuego = new JLabel("Juego (préstamo):");
		DefaultComboBoxModel<JuegoDeMesa> modJuegos = new DefaultComboBoxModel<>();
		for (JuegoDeMesa j : logica.getInventarioPrestamo()) {
			modJuegos.addElement(j);
		}
		JComboBox<JuegoDeMesa> cmbJuego = new JComboBox<>(modJuegos);

		JLabel lblMax = new JLabel("Max participantes:");
		JSpinner spnMax = new JSpinner(new SpinnerNumberModel(8, 2, 64, 1));

		JLabel lblTipo = new JLabel("Tipo:");
		String[] tipos = { "amistoso", "competitivo" };
		JComboBox<String> cmbTipo = new JComboBox<>(tipos);

		JButton btnCrear = new JButton("Crear");
		JButton btnCancelar = new JButton("Cancelar");

		btnCrear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa juego = (JuegoDeMesa) cmbJuego.getSelectedItem();
				if (juego == null) {
					JOptionPane.showMessageDialog(null, "No hay juegos en préstamo");
					return;
				}
				String dia = (String) cmbDia.getSelectedItem();
				int max = (Integer) spnMax.getValue();
				String tipo = (String) cmbTipo.getSelectedItem();
				try {
					logica.crearTorneo(admin, juego, dia, max, tipo);
					JOptionPane.showMessageDialog(null, "Torneo creado");
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

		panel.add(lblDia);
		panel.add(cmbDia);
		panel.add(lblJuego);
		panel.add(cmbJuego);
		panel.add(lblMax);
		panel.add(spnMax);
		panel.add(lblTipo);
		panel.add(cmbTipo);
		panel.add(btnCrear);
		panel.add(btnCancelar);
		add(panel);
	}
}
