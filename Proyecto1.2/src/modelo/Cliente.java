package modelo;

public class Cliente extends Usuario {

    // ===== ATRIBUTOS =====
    private double puntosFidelidad;



    // ===== CONSTRUCTOR =====
    public Cliente(String login, String password) {
        super(login, password);
        this.puntosFidelidad = 0;
    }



    // ===== MÉTODOS =====
    public double getPuntosFidelidad() { return puntosFidelidad; }
    public void setPuntosFidelidad(double puntosFidelidad) { this.puntosFidelidad = puntosFidelidad; }


    public void agregarPuntos(double puntos) {
        this.puntosFidelidad += puntos;
    }


    public boolean usarPuntos(double puntos) {
        if (puntos > this.puntosFidelidad) return false;
        this.puntosFidelidad -= puntos;
        return true;
    }
}
