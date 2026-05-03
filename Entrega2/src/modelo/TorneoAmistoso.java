package modelo;

public class TorneoAmistoso extends Torneo {


    private double bonoDescuento;

    // CONSTRUCTOR
    public TorneoAmistoso(JuegoDeMesa juego, String diaSemana, int maxParticipantes) {
        
        super(juego, diaSemana, maxParticipantes);
        
        this.bonoDescuento = 0.33;
    }


    public double getBonoDescuento() { return bonoDescuento; }
    public void setBonoDescuento(double bonoDescuento) { this.bonoDescuento = bonoDescuento; }
}