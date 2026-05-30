package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import modelo.JuegoDeMesa;

public class VentanaFavoritos extends JFrame {

	private VentanaCliente ventanaCliente;
	private DefaultListModel<JuegoDeMesa> modFavoritos;

	public VentanaFavoritos(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Gestionar Favoritos");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblFav = new JLabel("Tus favoritos:");
		modFavoritos = new DefaultListModel<>();
		recargarFavoritos();
		JList<JuegoDeMesa> listaFav = new JList<>(modFavoritos);
		JScrollPane scrollFav = new JScrollPane(listaFav);
		scrollFav.setPreferredSize(new Dimension(200, 150));

		JLabel lblTodos = new JLabel("Todos los juegos:");
		DefaultComboBoxModel<JuegoDeMesa> modTodos = new DefaultComboBoxModel<>();
		for (JuegoDeMesa j : ventanaCliente.getLogica().getInventarioPrestamo()) {
			modTodos.addElement(j);
		}
		for (JuegoDeMesa j : ventanaCliente.getLogica().getInventarioVenta()) {
			modTodos.addElement(j);
		}
		JComboBox<JuegoDeMesa> cmbTodos = new JComboBox<>(modTodos);

		JButton btnAgregar = new JButton("Agregar a favoritos");
		JButton btnQuitar = new JButton("Quitar de favoritos");
		JButton btnCerrar = new JButton("Cerrar");

		btnAgregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = (JuegoDeMesa) cmbTodos.getSelectedItem();
				if (j == null) return;
				ventanaCliente.getLogica().agregarFavorito(ventanaCliente.getClienteLogueado(), j);
				recargarFavoritos();
			}
		});

		btnQuitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JuegoDeMesa j = listaFav.getSelectedValue();
				if (j == null) {
					JOptionPane.showMessageDialog(null, "Selecciona un favorito a quitar");
					return;
				}
				ventanaCliente.getLogica().quitarFavorito(ventanaCliente.getClienteLogueado(), j);
				recargarFavoritos();
			}
		});

		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		panel.add(lblFav);
		panel.add(scrollFav);
		panel.add(lblTodos);
		panel.add(cmbTodos);
		panel.add(btnAgregar);
		panel.add(btnQuitar);
		panel.add(btnCerrar);
		add(panel);
	}

	private void recargarFavoritos() {
		modFavoritos.removeAllElements();
		for (JuegoDeMesa j : ventanaCliente.getClienteLogueado().getFavoritos()) {
			modFavoritos.addElement(j);
		}
	}
}
