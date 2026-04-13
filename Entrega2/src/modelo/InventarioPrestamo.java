package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioPrestamo {

    // Attributes
    private List<JuegoDeMesa> juegos = new ArrayList<>();



    // Constructor
    public InventarioPrestamo() {}



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
        return juegos.stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }


    public List<JuegoDeMesa> getDisponibles() {
        return juegos.stream()
                .filter(JuegoDeMesa::isDisponible)
                .collect(Collectors.toList());
    }


    public JuegoDeMesa buscarDisponiblePorNombre(String nombre) {
        return juegos.stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(nombre) && j.isDisponible())
                .findFirst()
                .orElse(null);
    }
}
