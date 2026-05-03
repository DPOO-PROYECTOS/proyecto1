package modelo;

import java.util.HashMap;
import java.util.Map;

public abstract class Torneo {
	protected JuegoDeMesa juego;
	protected String diaSemana;
	protected int maxParticipantes;
	protected Map<Usuario, Integer> inscripciones;
	protected int cuposDisponibles;
	protected int cuposFanaticosRestantes;
	
	
	public Torneo(JuegoDeMesa juego, String diaSemana, int maxParticipantes) {
        this.juego = juego;
        this.diaSemana = diaSemana;
        this.maxParticipantes = maxParticipantes;
        this.inscripciones = new HashMap<>();
        
        
        this.cuposFanaticosRestantes= (int) Math.ceil(maxParticipantes * 0.20);
        
        
        this.cuposDisponibles = maxParticipantes - this.cuposFanaticosRestantes;
    }
	
    public JuegoDeMesa getJuego() { return juego; }
    public void setJuego(JuegoDeMesa juego) { this.juego = juego; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public int getMaxParticipantes() { return maxParticipantes; }
    public void setMaxParticipantes(int maxParticipantes) { this.maxParticipantes = maxParticipantes; }

    public Map<Usuario, Integer> getInscripciones() { return inscripciones; }

    public int getCuposFanaticosRestantes() { return cuposFanaticosRestantes; }
    public void setCuposFanaticosRestantes(int cuposFanaticosRestantes) { this.cuposFanaticosRestantes = cuposFanaticosRestantes; }

    public int getCuposDisponibles() { return this.cuposDisponibles; }
    public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
}
