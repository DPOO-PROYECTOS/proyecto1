package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import modelo.JuegoDeMesa;

public class VentanaComprarJuego extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaComprarJuego(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Comprar Juego");
		setSize(500, 380);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblJuegos = new JLabel("Juegos a comprar (Ctrl+click para selección múltiple):");
		DefaultListModel<JuegoDeMesa> mod = new DefaultListModel<>();
		for (JuegoDeMesa j : ventanaCliente.getLogica().getInventarioVenta()) {
			mod.addElement(j);
		}
		JList<JuegoDeMesa> lista = new JList<>(mod);
		lista.setVisibleRowCount(6);
		JScrollPane scroll = new JScrollPane(lista);
		scroll.setPreferredSize(new Dimension(350, 130));

		JLabel lblCodigo = new JLabel("Código descuento empleado (opcional):");
		JTextField txtCodigo = new JTextField(10);

		JButton btnComprar = new JButton("Comprar");
		JButton btnCancelar = new JButton("Cancelar");

		btnComprar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<JuegoDeMesa> seleccionados = lista.getSelectedValuesList();
				if (seleccionados.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Selecciona al menos un juego");
					return;
				}
				String codigo = txtCodigo.getText();
				if (codigo.isBlank()) codigo = null;
				try {
					ventanaCliente.getLogica().venderJuegos(ventanaCliente.getClienteLogueado(), new ArrayList<>(seleccionados), codigo, 0, false);
					JOptionPane.showMessageDialog(null, "Compra realizada");
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

		panel.add(lblJuegos);
		panel.add(scroll);
		panel.add(lblCodigo);
		panel.add(txtCodigo);
		panel.add(btnComprar);
		panel.add(btnCancelar);
		add(panel);
	}
}
