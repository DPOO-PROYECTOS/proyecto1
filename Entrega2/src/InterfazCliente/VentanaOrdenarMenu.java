package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import modelo.ItemMenu;
import modelo.Mesa;

public class VentanaOrdenarMenu extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaOrdenarMenu(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Ordenar del Menú");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblItems = new JLabel("Items (Ctrl+click para selección múltiple):");
		List<ItemMenu> menu = ventanaCliente.getLogica().getCafe().getMenu();
		DefaultListModel<ItemMenu> mod = new DefaultListModel<>();
		for (ItemMenu it : menu) {
			mod.addElement(it);
		}
		JList<ItemMenu> lista = new JList<>(mod);
		lista.setVisibleRowCount(8);
		JScrollPane scroll = new JScrollPane(lista);
		scroll.setPreferredSize(new Dimension(350, 150));

		JLabel lblPropina = new JLabel("Propina:");
		JTextField txtPropina = new JTextField("0", 5);

		JButton btnOrdenar = new JButton("Ordenar");
		JButton btnCancelar = new JButton("Cancelar");

		btnOrdenar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				Mesa mesa = buscarMesaDelCliente();
				if (mesa == null) {
					JOptionPane.showMessageDialog(null, "Primero debes reservar una mesa");
					return;
				}
				try {
					ventanaCliente.getLogica().realizarPedidoCafe(ventanaCliente.getClienteLogueado(), mesa, new ArrayList<>(seleccionados), propina, 0, false);
					JOptionPane.showMessageDialog(null, "Pedido realizado");
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

		panel.add(lblItems);
		panel.add(scroll);
		panel.add(lblPropina);
		panel.add(txtPropina);
		panel.add(btnOrdenar);
		panel.add(btnCancelar);
		add(panel);
	}

	private Mesa buscarMesaDelCliente() {
		for (Mesa m : ventanaCliente.getLogica().getCafe().getMesas()) {
			if (ventanaCliente.getClienteLogueado().equals(m.getClienteAsignado())) {
				return m;
			}
		}
		return null;
	}
}
