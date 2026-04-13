package modelo;

import java.util.ArrayList;
import java.util.List;

public class InventarioVenta {

    // Attributes
    private List<JuegoDeMesa> juegos = new ArrayList<>();



    // Constructor
    public InventarioVenta() {}



    // Methods
    public List<JuegoDeMesa> getJuegos() { return juegos; }
    public void setJuegos(List<JuegoDeMesa> juegos) { this.juegos = juegos; }


    public void agregarJuego(JuegoDeMesa juego) {
        juegos.add(juego);
    }


    public void quitarJuego(JuegoDeMesa juego) {
        juegos.remove(juego);
    }


    public JuegoDeMesa buscarPorNombre(String nombre) {
        for (JuegoDeMesa j : juegos) {
            if (j.getNombre().equalsIgnoreCase(nombre)) {
                return j;
            }
        }
        return null;
    }


    public boolean estaEnInventario(JuegoDeMesa juego) {
        return juegos.contains(juego);
    }
}
