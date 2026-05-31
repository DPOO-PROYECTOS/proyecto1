package InterfazEmpleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import modelo.Turno;

public class VentanaVerTurnos extends JFrame {

	private VentanaEmpleado ventanaEmpleado;

	public VentanaVerTurnos(VentanaEmpleado ventanaEmpleado) {
		this.ventanaEmpleado = ventanaEmpleado;
		setTitle("Mis Turnos");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String[] cols = { "Día", "Inicio", "Fin" };
		DefaultTableModel mod = new DefaultTableModel(cols, 0);
		for (Turno t : ventanaEmpleado.getLogica().getTurnosDeEmpleado(ventanaEmpleado.getEmpleadoLogueado())) {
			Object[] fila = { t.getDia(), t.getHoraInicio(), t.getHoraFin() };
			mod.addRow(fila);
		}

		JTable tabla = new JTable(mod);
		JScrollPane scroll = new JScrollPane(tabla);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		JPanel sur = new JPanel();
		sur.add(btnCerrar);
		add(sur, BorderLayout.SOUTH);
	}
}
