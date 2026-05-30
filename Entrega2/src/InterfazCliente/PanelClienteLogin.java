package InterfazCliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logica.CafeLogica;
import modelo.Cliente;
import modelo.Usuario;

public class PanelClienteLogin extends JPanel{

	private CafeLogica logica;
	private VentanaCliente ventana;

	public PanelClienteLogin(CafeLogica logica, VentanaCliente ventana){
		this.logica = logica;
		this.ventana = ventana;
		setBackground(Color.LIGHT_GRAY);

		JLabel lblUsuario = new JLabel("Usuario:");
        JTextField txtUsuario = new JTextField(15);

        JLabel lblPassword = new JLabel("Contraseña:");
        JPasswordField txtPassword = new JPasswordField(15);
        JButton btnIngresar= new JButton("Ingresar");

        btnIngresar.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String usuarioIngresado=txtUsuario.getText();

        		String paddwordIngresada= new String(txtPassword.getPassword());

        		try {
        			Usuario userTemp= logica.login(usuarioIngresado, paddwordIngresada);
        			if (userTemp instanceof Cliente) {
        				ventana.setClienteLogueado((Cliente) userTemp);
        				JOptionPane.showMessageDialog(null,  "Bievenido "+ usuarioIngresado);
        				ventana.mostrarVentanaOpciones();

        			}else {
        				JOptionPane.showMessageDialog(null, "Este usuario no es un cliente");

        			}
        		} catch(Exception ex) {
        			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        		}
        	}
        });




        add(lblUsuario);
        add(txtUsuario);
        add(lblPassword);
        add(txtPassword);
        add(btnIngresar);
	}
}