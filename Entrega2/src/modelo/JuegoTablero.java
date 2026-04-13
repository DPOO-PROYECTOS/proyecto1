package modelo;

public class JuegoTablero extends JuegoDeMesa {


    // Constructor
    public JuegoTablero(String nombre, int anioPublicacion, String empresaMatriz,
                        int minJugadores, int maxJugadores, boolean aptaMenores5,
                        boolean soloAdultos, String estado, boolean disponible) {
        super(nombre, anioPublicacion, empresaMatriz, minJugadores, maxJugadores,
              aptaMenores5, soloAdultos, estado, disponible);
    }


}
