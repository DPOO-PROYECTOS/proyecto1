package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import logica.CafeLogica;
import modelo.JuegoDeMesa;

public class VentanaCatalogo extends JFrame {

	private CafeLogica logica;
	private boolean esVenta;

	public VentanaCatalogo(CafeLogica logica, boolean esVenta) {
		this.logica = logica;
		this.esVenta = esVenta;
		setTitle("Catálogo — Inventario " + (esVenta ? "Venta" : "Préstamo"));
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String[] columnas = { "Nombre", "Año", "Editor", "Estado", "Precio" };
		DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

		List<JuegoDeMesa> juegos;
		if (esVenta) {
			juegos = logica.getInventarioVenta();
		} else {
			juegos = logica.getInventarioPrestamo();
		}

		for (JuegoDeMesa j : juegos) {
			Object[] fila = new Object[5];
			fila[0] = j.getNombre();
			fila[1] = j.getAnioPublicacion();
			fila[2] = j.getEmpresaMatriz();
			fila[3] = j.getEstado();
			fila[4] = j.getPrecioVenta();
			modelo.addRow(fila);
		}

		JTable tabla = new JTable(modelo);
		JScrollPane scroll = new JScrollPane(tabla);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		JPanel sur = new JPanel();
		sur.add(btnCerrar);
		add(sur, BorderLayout.SOUTH);
	}
}
