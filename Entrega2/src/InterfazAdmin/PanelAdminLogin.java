package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Admin;
import modelo.Usuario;

public class PanelAdminLogin extends JPanel {

	private CafeLogica logica;
	private VentanaAdmin ventana;

	public PanelAdminLogin(CafeLogica logica, VentanaAdmin ventana) {
		this.logica = logica;
		this.ventana = ventana;
		setBackground(Color.LIGHT_GRAY);

		JLabel lblUsuario = new JLabel("Usuario:");
		JTextField txtUsuario = new JTextField(15);

		JLabel lblPassword = new JLabel("Contraseña:");
		JPasswordField txtPassword = new JPasswordField(15);
		JButton btnIngresar = new JButton("Ingresar");

		btnIngresar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String usuarioIngresado = txtUsuario.getText();
				String passwordIngresada = new String(txtPassword.getPassword());

				try {
					Usuario userTemp = logica.login(usuarioIngresado, passwordIngresada);
					if (userTemp instanceof Admin) {
						ventana.setAdminLogueado((Admin) userTemp);
						JOptionPane.showMessageDialog(null, "Bienvenido " + usuarioIngresado);
						ventana.mostrarMenuAdmin();
					} else {
						JOptionPane.showMessageDialog(null, "Este usuario no es admin");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		add(lblUsuario);
		add(txtUsuario);
		add(lblPassword);
		add(txtPassword);
		add(btnIngresar);
	}
}
