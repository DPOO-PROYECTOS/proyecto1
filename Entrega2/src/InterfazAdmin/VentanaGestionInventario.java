package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.JuegoDeMesa;

public class VentanaGestionInventario extends JFrame {

	private CafeLogica logica;

	public VentanaGestionInventario(CafeLogica logica) {
		this.logica = logica;
		setTitle("Gestionar Inventario");
		setSize(450, 280);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lbl = new JLabel("Juego (inv. préstamo):");
		DefaultComboBoxModel<JuegoDeMesa> mod = new DefaultComboBoxModel<>();
		for (JuegoDeMesa j : logica.getInventarioPrestamo()) {
			mod.addElement(j);
		}
		JComboBox<JuegoDeMesa> cmb = new JComboBox<>(mod);

		JLabel lblEstado = new JLabel("Nuevo estado:");
		String[] estados = { "Nuevo", "Usado", "Desgastado", "Roto" };
		JComboBox<String> cmbEstado = new JComboBox<>(estados);

		JButton btnReparar = new JButton("Reparar");
		JButton btnRobado = new JButton("Marcar Robado");
		JButton btnEstado = new JButton("Cambiar Estado");
		JButton btnCerrar = new JButton("Cerrar");

		btnReparar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = (JuegoDeMesa) cmb.getSelectedItem();
				if (j == null) { JOptionPane.showMessageDialog(null, "No hay juego seleccionado"); return; }
				try {
					logica.repararJuego(j);
					JOptionPane.showMessageDialog(null, "Juego reparado");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnRobado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = (JuegoDeMesa) cmb.getSelectedItem();
				if (j == null) { JOptionPane.showMessageDialog(null, "No hay juego seleccionado"); return; }
				try {
					logica.marcarComoRobado(j);
					JOptionPane.showMessageDialog(null, "Juego marcado como robado");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnEstado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = (JuegoDeMesa) cmb.getSelectedItem();
				if (j == null) { JOptionPane.showMessageDialog(null, "No hay juego seleccionado"); return; }
				try {
					logica.actualizarEstadoJuego(j, (String) cmbEstado.getSelectedItem());
					JOptionPane.showMessageDialog(null, "Estado actualizado");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lbl);
		panel.add(cmb);
		panel.add(lblEstado);
		panel.add(cmbEstado);
		panel.add(btnReparar);
		panel.add(btnRobado);
		panel.add(btnEstado);
		panel.add(btnCerrar);
		add(panel);
	}
}
