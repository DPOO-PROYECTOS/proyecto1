package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;

public class VentanaRegistrarEmpleado extends JFrame {

	private CafeLogica logica;

	public VentanaRegistrarEmpleado(CafeLogica logica) {
		this.logica = logica;
		setTitle("Registrar Empleado");
		setSize(400, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblUsuario = new JLabel("Usuario:");
		JTextField txtUsuario = new JTextField(15);
		JLabel lblPass = new JLabel("Contraseña:");
		JPasswordField txtPass = new JPasswordField(15);
		JLabel lblTipo = new JLabel("Tipo:");
		String[] tipos = { "mesero", "cocinero" };
		JComboBox<String> cmbTipo = new JComboBox<>(tipos);

		JButton btnRegistrar = new JButton("Registrar");
		JButton btnCancelar = new JButton("Cancelar");

		btnRegistrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = txtUsuario.getText();
				String pass = new String(txtPass.getPassword());
				String tipo = (String) cmbTipo.getSelectedItem();
				try {
					logica.registrarEmpleado(user, pass, tipo);
					JOptionPane.showMessageDialog(null, "Empleado registrado");
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

		panel.add(lblUsuario);
		panel.add(txtUsuario);
		panel.add(lblPass);
		panel.add(txtPass);
		panel.add(lblTipo);
		panel.add(cmbTipo);
		panel.add(btnRegistrar);
		panel.add(btnCancelar);
		add(panel);
	}
}
