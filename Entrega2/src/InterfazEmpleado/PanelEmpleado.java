package InterfazEmpleado;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing. JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import logica.CafeLogica;
import modelo.Usuario;
import javax.swing.JOptionPane;


public class PanelEmpleado extends JPanel
{
	private CafeLogica logica;
	public PanelEmpleado(CafeLogica logica) {
		this.logica = logica;
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
        			if (userTemp != null) {
        				JOptionPane.showMessageDialog(null,  "Bievenido "+ usuarioIngresado);
        	
        			}else {
        				JOptionPane.showMessageDialog(null, "usuario o contraseña incorrectos");
        				
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
