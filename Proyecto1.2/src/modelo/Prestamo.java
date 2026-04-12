package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Prestamo {

    // ===== ATRIBUTOS =====
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<JuegoDeMesa> juegos = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    protected Prestamo(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }



    // ===== MÉTODOS =====
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public List<JuegoDeMesa> getJuegos() { return juegos; }
    public void setJuegos(List<JuegoDeMesa> juegos) { this.juegos = juegos; }


    public void agregarJuego(JuegoDeMesa juego) {
        juegos.add(juego);
        juego.setDisponible(false);
        juego.incrementarPrestamos();
    }


    public void devolverJuego(JuegoDeMesa juego) {
        juegos.remove(juego);
        juego.setDisponible(true);
    }


    public boolean estaActivo() {
        return fechaFin == null;
    }


    public void finalizar() {
        this.fechaFin = LocalDateTime.now();
        for (JuegoDeMesa juego : juegos) {
            juego.setDisponible(true);
        }
    }
}
