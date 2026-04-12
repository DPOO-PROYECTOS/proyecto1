package logica;

import java.util.List;

import modelo.Cafe;
import modelo.Empleado;
import modelo.ItemMenu;
import modelo.Mesa;
import modelo.Prestamo;
import modelo.SolicitudCambioTurno;
import modelo.Usuario;
import modelo.VentaCafeteria;
import modelo.VentaJuego;

public class CafeLogica {

    // Attributes
    private Cafe cafe;



    // Constructor
    public CafeLogica(String nombreCafe, int capacidadMaxima) {
        this.cafe = new Cafe(nombreCafe, capacidadMaxima);
    }


    
    
    // == FUNCIONES DE LOGICA ==
    

    // usuarios
    public Usuario login(String login, String password) {
        return null;
    }



    // mesas
    public Mesa asignarMesa(String login, int numPersonas, boolean tieneNinos, boolean tieneJovenes) {
        return null;
    }

    public void liberarMesa(int numeroMesa) {

    }



    // ventas cafeteria
    public VentaCafeteria registrarVentaCafeteria(String login, List<String> nombresItems, List<Integer> cantidades, String codigoEmpleado, double propina) {
        return null;
    }



    // ventas juego
    public VentaJuego registrarVentaJuego(String login, List<String> nombresJuegos, List<Integer> cantidades, String codigoEmpleado) {
    	
        return null;
    }



    // prestamos
    public Prestamo iniciarPrestamo(String login, List<String> nombresJuegos) {
        return null;
    }

    public void devolverPrestamo(Prestamo prestamo) {

    }



    // turnos
    public void asignarTurno(String loginEmpleado, String dia, String horaInicio, String horaFin) {

    }

    public void solicitarCambioTurno(String loginSolicitante, String loginIntercambio, modelo.Turno turno) {

    }

    public void resolverSolicitud(String loginAdmin, SolicitudCambioTurno solicitud, boolean aprobar) {

    }



    // menu
    public void agregarItemMenu(ItemMenu item) {

    }

    public void sugerirPlatillo(String loginEmpleado, String descripcion, ItemMenu item) {

    }
}
