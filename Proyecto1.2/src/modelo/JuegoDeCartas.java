package modelo;

public class JuegoDeCartas extends JuegoDeMesa {

    // Attributes



    // Constructor
    public JuegoDeCartas(String nombre, int anioPublicacion, String empresaMatriz,
                         int minJugadores, int maxJugadores, boolean aptaMenores5,
                         boolean soloAdultos, String estado, boolean disponible) {
        super(nombre, anioPublicacion, empresaMatriz, minJugadores, maxJugadores,
              aptaMenores5, soloAdultos, estado, disponible);
    }


}
