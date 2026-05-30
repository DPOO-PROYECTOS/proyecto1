package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import logica.CafeLogica;
import modelo.JuegoDeMesa;
import modelo.Prestamo;
import modelo.PrestamoCliente;

public class VentanaHistorialPrestamos extends JFrame {

	private CafeLogica logica;

	public VentanaHistorialPrestamos(CafeLogica logica) {
		this.logica = logica;
		setTitle("Historial de Préstamos");
		setSize(700, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String[] columnas = { "Tipo", "Inicio", "Fin", "Juegos", "Mesa" };
		DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

		for (Prestamo p : logica.getCafe().getHistorialPrestamos()) {
			Object[] fila = new Object[5];
			fila[0] = (p instanceof PrestamoCliente) ? "Cliente" : "Empleado";
			fila[1] = p.getFechaInicio();
			fila[2] = p.getFechaFin() == null ? "(activo)" : p.getFechaFin();
			StringBuilder nombres = new StringBuilder();
			for (JuegoDeMesa j : p.getJuegos()) {
				if (nombres.length() > 0) nombres.append(", ");
				nombres.append(j.getNombre());
			}
			fila[3] = nombres.toString();
			if (p instanceof PrestamoCliente) {
				fila[4] = ((PrestamoCliente) p).getMesa() != null ? ((PrestamoCliente) p).getMesa().getNumero() : "-";
			} else {
				fila[4] = "-";
			}
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
