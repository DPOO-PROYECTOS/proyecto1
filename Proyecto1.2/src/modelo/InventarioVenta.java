package modelo;

import java.util.ArrayList;
import java.util.List;

public class InventarioVenta {

    // ===== ATRIBUTOS =====
    private List<JuegoDeMesa> juegos = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    public InventarioVenta() {}



    // ===== MÉTODOS =====
    public List<JuegoDeMesa> getJuegos() { return juegos; }
    public void setJuegos(List<JuegoDeMesa> juegos) { this.juegos = juegos; }


    public void agregarJuego(JuegoDeMesa juego) {
        juegos.add(juego);
    }


    public void quitarJuego(JuegoDeMesa juego) {
        juegos.remove(juego);
    }


    public JuegoDeMesa buscarPorNombre(String nombre) {
        return juegos.stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }


    public boolean estaEnInventario(JuegoDeMesa juego) {
        return juegos.contains(juego);
    }
}
