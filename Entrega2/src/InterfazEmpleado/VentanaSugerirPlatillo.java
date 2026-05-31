package InterfazEmpleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.Bebida;
import modelo.ItemMenu;
import modelo.Pasteleria;

public class VentanaSugerirPlatillo extends JFrame {

	private VentanaEmpleado ventanaEmpleado;

	public VentanaSugerirPlatillo(VentanaEmpleado ventanaEmpleado) {
		this.ventanaEmpleado = ventanaEmpleado;
		setTitle("Sugerir Platillo");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblDesc = new JLabel("Descripción de la sugerencia:");
		JTextArea txtDesc = new JTextArea(3, 25);
		JScrollPane scrollDesc = new JScrollPane(txtDesc);

		JLabel lblTipo = new JLabel("Tipo de item:");
		String[] tipos = { "Bebida", "Pasteleria" };
		JComboBox<String> cmbTipo = new JComboBox<>(tipos);

		JLabel lblNombre = new JLabel("Nombre item:");
		JTextField txtNombre = new JTextField(15);

		JLabel lblPrecio = new JLabel("Precio:");
		JTextField txtPrecio = new JTextField(8);

		JCheckBox chkAlcoholica = new JCheckBox("Alcohólica");
		JCheckBox chkCaliente = new JCheckBox("Caliente");

		JButton btnSugerir = new JButton("Sugerir");
		JButton btnCancelar = new JButton("Cancelar");

		btnSugerir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String desc = txtDesc.getText();
				String nombre = txtNombre.getText();
				if (desc.isBlank() || nombre.isBlank()) {
					JOptionPane.showMessageDialog(null, "Descripción y nombre son obligatorios");
					return;
				}
				double precio;
				try {
					precio = Double.parseDouble(txtPrecio.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Precio inválido");
					return;
				}
				ItemMenu item;
				if (cmbTipo.getSelectedItem().equals("Bebida")) {
					item = new Bebida(nombre, precio, chkAlcoholica.isSelected(), chkCaliente.isSelected());
				} else {
					item = new Pasteleria(nombre, precio);
				}
				try {
					ventanaEmpleado.getLogica().sugerirPlatillo(ventanaEmpleado.getEmpleadoLogueado(), desc, item);
					JOptionPane.showMessageDialog(null, "Sugerencia enviada al admin");
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lblDesc);
		panel.add(scrollDesc);
		panel.add(lblTipo);
		panel.add(cmbTipo);
		panel.add(lblNombre);
		panel.add(txtNombre);
		panel.add(lblPrecio);
		panel.add(txtPrecio);
		panel.add(chkAlcoholica);
		panel.add(chkCaliente);
		panel.add(btnSugerir);
		panel.add(btnCancelar);
		add(panel);
	}
}
