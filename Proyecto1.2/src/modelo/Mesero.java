package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mesero extends Empleado {

    // ===== ATRIBUTOS =====
    private List<JuegoDificil> juegosConocidos = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    public Mesero(String login, String password) {
        super(login, password, "mesero");
    }



    // ===== MÉTODOS =====
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
