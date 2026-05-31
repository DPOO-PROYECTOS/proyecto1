package InterfazEmpleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import modelo.ItemMenu;
import modelo.Mesa;
import modelo.Mesero;

public class VentanaTomarPedido extends JFrame {

	private VentanaEmpleado ventanaEmpleado;

	public VentanaTomarPedido(VentanaEmpleado ventanaEmpleado) {
		this.ventanaEmpleado = ventanaEmpleado;

		// solo meseros pueden tomar pedidos
		if (!(ventanaEmpleado.getEmpleadoLogueado() instanceof Mesero)) {
			JOptionPane.showMessageDialog(null, "Solo los meseros pueden tomar pedidos");
			dispose();
			return;
		}

		setTitle("Tomar Pedido");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblMesa = new JLabel("Mesa ocupada:");
		DefaultComboBoxModel<Mesa> modMesas = new DefaultComboBoxModel<>();
		for (Mesa m : ventanaEmpleado.getLogica().getCafe().getMesas()) {
			if (!m.estaDisponible()) modMesas.addElement(m);
		}
		JComboBox<Mesa> cmbMesa = new JComboBox<>(modMesas);

		JLabel lblItems = new JLabel("Items (Ctrl+click selección múltiple):");
		List<ItemMenu> menu = ventanaEmpleado.getLogica().getCafe().getMenu();
		DefaultListModel<ItemMenu> modItems = new DefaultListModel<>();
		for (ItemMenu it : menu) modItems.addElement(it);
		JList<ItemMenu> lista = new JList<>(modItems);
		lista.setVisibleRowCount(6);
		JScrollPane scroll = new JScrollPane(lista);
		scroll.setPreferredSize(new Dimension(350, 130));

		JLabel lblPropina = new JLabel("Propina:");
		JTextField txtPropina = new JTextField("0", 5);

		JButton btnConfirmar = new JButton("Confirmar Pedido");
		JButton btnCancelar = new JButton("Cancelar");

		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mesa mesa = (Mesa) cmbMesa.getSelectedItem();
				if (mesa == null) {
					JOptionPane.showMessageDialog(null, "No hay mesa con cliente seleccionada");
					return;
				}
				List<ItemMenu> seleccionados = lista.getSelectedValuesList();
				if (seleccionados.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Selecciona al menos un item");
					return;
				}
				double propina;
				try {
					propina = Double.parseDouble(txtPropina.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Propina inválida");
					return;
				}
				try {
					ventanaEmpleado.getLogica().realizarPedidoCafe(mesa.getClienteAsignado(), mesa, new ArrayList<>(seleccionados), propina, 0, false);
					JOptionPane.showMessageDialog(null, "Pedido registrado");
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lblMesa);
		panel.add(cmbMesa);
		panel.add(lblItems);
		panel.add(scroll);
		panel.add(lblPropina);
		panel.add(txtPropina);
		panel.add(btnConfirmar);
		panel.add(btnCancelar);
		add(panel);
	}
}
