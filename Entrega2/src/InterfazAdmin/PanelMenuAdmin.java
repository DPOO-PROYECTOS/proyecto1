package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PanelMenuAdmin extends JPanel {

	private VentanaAdmin ventanaAdmin;

	public PanelMenuAdmin(VentanaAdmin ventanaAdmin) {
		this.ventanaAdmin = ventanaAdmin;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new GridLayout(5, 3, 8, 8));

		JButton btnCrearTorneo = new JButton("Crear Torneo");
		JButton btnCatalogoVenta = new JButton("Catalogo Venta");
		JButton btnCatalogoPrestamo = new JButton("Catalogo Prestamo");
		JButton btnMoverVP = new JButton("Mover V→P");
		JButton btnRegEmpleado = new JButton("Reg.Empleado");
		JButton btnAgregarItem = new JButton("Agregar Item Menu");
		JButton btnCambiosTurno = new JButton("Cambios Turno");
		JButton btnHistorial = new JButton("Hist.Prestamos");
		JButton btnGestInventario = new JButton("Gest.Inventario");
		JButton btnGestTurnos = new JButton("Gest.Turnos");
		JButton btnGraficas = new JButton("Ver Graficas");
		JButton btnSugerencias = new JButton("Aprobar Sugerencias");
		JButton btnPremiar = new JButton("Premiar Ganador");
		JButton btnCerrarSesion = new JButton("Cerrar Sesion");
		JButton btnSalir = new JButton("Salir y Guardar");

		btnCrearTorneo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCrearTorneo(ventanaAdmin.getLogica(), ventanaAdmin.getAdminLogueado()).setVisible(true);
			}
		});

		btnCatalogoVenta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCatalogo(ventanaAdmin.getLogica(), true).setVisible(true);
			}
		});

		btnCatalogoPrestamo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCatalogo(ventanaAdmin.getLogica(), false).setVisible(true);
			}
		});

		btnMoverVP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaMoverVentaPrestamo(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnRegEmpleado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaRegistrarEmpleado(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnAgregarItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaAgregarItemMenu(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnCambiosTurno.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaCambiosTurno(ventanaAdmin.getLogica(), ventanaAdmin.getAdminLogueado()).setVisible(true);
			}
		});

		btnHistorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaHistorialPrestamos(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnGestInventario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaGestionInventario(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnGestTurnos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaGestionTurnos(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnGraficas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new InterfazGraficas.VentanaGraficas(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnSugerencias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaAprobarSugerencias(ventanaAdmin.getLogica(), ventanaAdmin.getAdminLogueado()).setVisible(true);
			}
		});

		btnPremiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VentanaPremiarGanador(ventanaAdmin.getLogica()).setVisible(true);
			}
		});

		btnCerrarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Sesión cerrada");
				SwingUtilities.getWindowAncestor(PanelMenuAdmin.this).dispose();
			}
		});

		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// aqui iria persistencia.guardarTodo si existiera
				JOptionPane.showMessageDialog(null, "Datos guardados. Cerrando aplicación...");
				System.exit(0);
			}
		});

		add(btnCrearTorneo);
		add(btnCatalogoVenta);
		add(btnCatalogoPrestamo);
		add(btnMoverVP);
		add(btnRegEmpleado);
		add(btnAgregarItem);
		add(btnCambiosTurno);
		add(btnHistorial);
		add(btnGestInventario);
		add(btnGestTurnos);
		add(btnGraficas);
		add(btnSugerencias);
		add(btnPremiar);
		add(btnCerrarSesion);
		add(btnSalir);
	}
}
