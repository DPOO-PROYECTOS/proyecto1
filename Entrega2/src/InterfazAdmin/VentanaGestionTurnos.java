package InterfazAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import logica.CafeLogica;
import modelo.Empleado;
import modelo.Turno;
import modelo.Usuario;

public class VentanaGestionTurnos extends JFrame {

	private CafeLogica logica;
	private DefaultTableModel tableModel;

	public VentanaGestionTurnos(CafeLogica logica) {
		this.logica = logica;
		setTitle("Gestionar Turnos de Empleados");
		setSize(700, 450);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel form = new JPanel();
		form.setBackground(Color.LIGHT_GRAY);

		JLabel lblEmp = new JLabel("Empleado:");
		DefaultComboBoxModel<Empleado> modEmp = new DefaultComboBoxModel<>();
		for (Usuario u : logica.getCafe().getUsuarios()) {
			if (u instanceof Empleado) modEmp.addElement((Empleado) u);
		}
		JComboBox<Empleado> cmbEmp = new JComboBox<>(modEmp);

		JLabel lblDia = new JLabel("Día:");
		String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
		JComboBox<String> cmbDia = new JComboBox<>(dias);

		JLabel lblHIni = new JLabel("Hora inicio:");
		JTextField txtHIni = new JTextField("08:00", 5);
		JLabel lblHFin = new JLabel("Hora fin:");
		JTextField txtHFin = new JTextField("16:00", 5);

		JButton btnCrear = new JButton("Crear Turno");

		form.add(lblEmp); form.add(cmbEmp);
		form.add(lblDia); form.add(cmbDia);
		form.add(lblHIni); form.add(txtHIni);
		form.add(lblHFin); form.add(txtHFin);
		form.add(btnCrear);

		String[] cols = { "Empleado", "Día", "Inicio", "Fin" };
		tableModel = new DefaultTableModel(cols, 0);
		recargarTabla();
		JTable tabla = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(tabla);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});

		btnCrear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Empleado emp = (Empleado) cmbEmp.getSelectedItem();
				if (emp == null) { JOptionPane.showMessageDialog(null, "Selecciona un empleado"); return; }
				try {
					logica.crearTurno(emp, (String) cmbDia.getSelectedItem(), txtHIni.getText(), txtHFin.getText());
					recargarTabla();
					JOptionPane.showMessageDialog(null, "Turno creado");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
				}
			}
		});

		setLayout(new BorderLayout());
		add(form, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		JPanel sur = new JPanel();
		sur.add(btnCerrar);
		add(sur, BorderLayout.SOUTH);
	}

	private void recargarTabla() {
		tableModel.setRowCount(0);
		for (Usuario u : logica.getCafe().getUsuarios()) {
			if (u instanceof Empleado) {
				Empleado emp = (Empleado) u;
				for (Turno t : logica.getTurnosDeEmpleado(emp)) {
					Object[] fila = { emp.getLogin(), t.getDia(), t.getHoraInicio(), t.getHoraFin() };
					tableModel.addRow(fila);
				}
			}
		}
	}
}
