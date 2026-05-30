package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Bebida;
import modelo.ItemMenu;
import modelo.Pasteleria;

public class VentanaAgregarItemMenu extends JFrame {

	private CafeLogica logica;

	public VentanaAgregarItemMenu(CafeLogica logica) {
		this.logica = logica;
		setTitle("Agregar Item al Menú");
		setSize(420, 320);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblTipo = new JLabel("Tipo:");
		String[] tipos = { "Bebida", "Pasteleria" };
		JComboBox<String> cmbTipo = new JComboBox<>(tipos);

		JLabel lblNombre = new JLabel("Nombre:");
		JTextField txtNombre = new JTextField(15);

		JLabel lblPrecio = new JLabel("Precio:");
		JTextField txtPrecio = new JTextField(8);

		JCheckBox chkAlcoholica = new JCheckBox("Alcohólica");
		JCheckBox chkCaliente = new JCheckBox("Caliente");
		// solo aplica para bebida

		JButton btnAgregar = new JButton("Agregar");
		JButton btnCancelar = new JButton("Cancelar");

		btnAgregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tipo = (String) cmbTipo.getSelectedItem();
				String nombre = txtNombre.getText();
				double precio;
				try {
					precio = Double.parseDouble(txtPrecio.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Precio inválido");
					return;
				}
				ItemMenu item;
				if (tipo.equals("Bebida")) {
					item = new Bebida(nombre, precio, chkAlcoholica.isSelected(), chkCaliente.isSelected());
				} else {
					item = new Pasteleria(nombre, precio);
				}
				try {
					logica.agregarItemMenu(item);
					JOptionPane.showMessageDialog(null, "Item agregado al menú");
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

		panel.add(lblTipo);
		panel.add(cmbTipo);
		panel.add(lblNombre);
		panel.add(txtNombre);
		panel.add(lblPrecio);
		panel.add(txtPrecio);
		panel.add(chkAlcoholica);
		panel.add(chkCaliente);
		panel.add(btnAgregar);
		panel.add(btnCancelar);
		add(panel);
	}
}
