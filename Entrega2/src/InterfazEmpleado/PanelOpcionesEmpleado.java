package InterfazEmpleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PanelOpcionesEmpleado extends JPanel {

	private VentanaEmpleado ventanaEmpleado;

	public PanelOpcionesEmpleado(VentanaEmpleado ventanaEmpleado) {
		this.ventanaEmpleado = ventanaEmpleado;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new GridLayout(2, 3, 8, 8));

		JButton btnVerTurnos = new JButton("Ver Mis Turnos");
		JButton btnSolicitarCambio = new JButton("Solicitar Cambio Turno");
		JButton btnTomarPedido = new JButton("Tomar Pedido (Mesero)");
		JButton btnSugerir = new JButton("Sugerir Platillo");
		JButton btnSalir = new JButton("Salir y Guardar");

		btnVerTurnos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaVerTurnos(ventanaEmpleado).setVisible(true);
			}
		});

		btnSolicitarCambio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaSolicitarCambioTurno(ventanaEmpleado).setVisible(true);
			}
		});

		btnTomarPedido.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaTomarPedido(ventanaEmpleado).setVisible(true);
			}
		});

		btnSugerir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaSugerirPlatillo(ventanaEmpleado).setVisible(true);
			}
		});

		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Datos guardados. Cerrando aplicación...");
				System.exit(0);
			}
		});

		add(btnVerTurnos);
		add(btnSolicitarCambio);
		add(btnTomarPedido);
		add(btnSugerir);
		add(btnSalir);
	}
}
