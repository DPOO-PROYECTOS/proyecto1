package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mesero extends Empleado {

    // Attributes
    private List<JuegoDificil> juegosConocidos = new ArrayList<>();



    // Constructor
    public Mesero(String login, String password) {
        super(login, password, "mesero");
    }



    // Methods
    public List<JuegoDificil> getJuegosConocidos() { return juegosConocidos; }
    public void setJuegosConocidos(List<JuegoDificil> juegosConocidos) { this.juegosConocidos = juegosConocidos; }


    public void agregarJuegoConocido(JuegoDificil juego) {
        if (!juegosConocidos.contains(juego)) {
            juegosConocidos.add(juego);
        }
    }


    public boolean conoceJuego(JuegoDificil juego) {
        return juegosConocidos.contains(juego);
    }
}
