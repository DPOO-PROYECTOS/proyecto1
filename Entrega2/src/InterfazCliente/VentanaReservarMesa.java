package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class VentanaReservarMesa extends JFrame {

	private VentanaCliente ventanaCliente;

	public VentanaReservarMesa(VentanaCliente ventanaCliente) {
		this.ventanaCliente = ventanaCliente;
		setTitle("Reservar Mesa");
		setSize(400, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);

		JLabel lblPersonas = new JLabel("Número de personas:");
		JSpinner spnPersonas = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

		JCheckBox chkNinos = new JCheckBox("Hay niños");
		JCheckBox chkJovenes = new JCheckBox("Hay jóvenes");

		JButton btnReservar = new JButton("Reservar");
		JButton btnCancelar = new JButton("Cancelar");

		btnReservar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int num = (Integer) spnPersonas.getValue();
					ventanaCliente.getLogica().asignarMesa(ventanaCliente.getClienteLogueado(), num, chkNinos.isSelected(), chkJovenes.isSelected());
					JOptionPane.showMessageDialog(null, "Mesa reservada");
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

		panel.add(lblPersonas);
		panel.add(spnPersonas);
		panel.add(chkNinos);
		panel.add(chkJovenes);
		panel.add(btnReservar);
		panel.add(btnCancelar);
		add(panel);
	}
}
