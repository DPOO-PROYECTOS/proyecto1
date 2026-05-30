package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import InterfazAdmin.VentanaCatalogo;

public class PanelOpcionesCliente extends JPanel {

	private VentanaCliente ventanaCliente;

	public PanelOpcionesCliente(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new GridLayout(3, 3, 8, 8));

		JButton btnCatalogoV = new JButton("Catálogo Venta");
		JButton btnCatalogoP = new JButton("Catálogo Préstamo");
		JButton btnReservarMesa = new JButton("Reservar Mesa");
		JButton btnOrdenar = new JButton("Ordenar del Menú");
		JButton btnPrestamo = new JButton("Solicitar Préstamo");
		JButton btnComprar = new JButton("Comprar Juego");
		JButton btnInscTorneo = new JButton("Inscribirse Torneo");
		JButton btnDesinscTorneo = new JButton("Desinscribirse Torneo");
		JButton btnFavoritos = new JButton("Gestionar Favoritos");

		btnCatalogoV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCatalogo(ventanaCliente.getLogica(), true).setVisible(true);
			}
		});

		btnCatalogoP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCatalogo(ventanaCliente.getLogica(), false).setVisible(true);
			}
		});

		btnReservarMesa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaReservarMesa(ventanaCliente).setVisible(true);
			}
		});

		btnOrdenar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaOrdenarMenu(ventanaCliente).setVisible(true);
			}
		});

		btnPrestamo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaSolicitarPrestamo(ventanaCliente).setVisible(true);
			}
		});

		btnComprar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaComprarJuego(ventanaCliente).setVisible(true);
			}
		});

		btnInscTorneo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaInscribirTorneo(ventanaCliente).setVisible(true);
			}
		});

		btnDesinscTorneo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaDesinscribirTorneo(ventanaCliente).setVisible(true);
			}
		});

		btnFavoritos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaFavoritos(ventanaCliente).setVisible(true);
			}
		});

		add(btnCatalogoV);
		add(btnCatalogoP);
		add(btnReservarMesa);
		add(btnOrdenar);
		add(btnPrestamo);
		add(btnComprar);
		add(btnInscTorneo);
		add(btnDesinscTorneo);
		add(btnFavoritos);
	}
}
