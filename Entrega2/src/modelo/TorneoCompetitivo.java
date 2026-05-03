package modelo;

public class TorneoCompetitivo extends Torneo {

    private double tarifaEntrada;
    private double PremioAcumulado;

    // CONSTRUCTOR
    public TorneoCompetitivo(JuegoDeMesa juego, String diaSemana, int maxParticipantes, double tarifaEntrada) {

        super(juego, diaSemana, maxParticipantes);
        
        this.tarifaEntrada = tarifaEntrada;
        this.PremioAcumulado = 0.0; 
    }

    // GETTERS Y SETTERS
    public double getTarifaEntrada() { return tarifaEntrada; }
    public void setTarifaEntrada(double tarifaEntrada) { this.tarifaEntrada = tarifaEntrada; }

    public double getPremioAcumulado() { return PremioAcumulado; }
    
    public void sumarAlPozo(double monto) {
        this.PremioAcumulado += monto;
    }
}