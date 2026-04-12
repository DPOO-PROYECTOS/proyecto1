package modelo;

import java.util.ArrayList;
import java.util.List;

public class PlanSemanal {

    // Attributes
    private List<Turno> turnos = new ArrayList<>();



    // Constructor
    public PlanSemanal() {}



    // ===== MÉTODOS =====
    public List<Turno> getTurnos() { return turnos; }
    public void setTurnos(List<Turno> turnos) { this.turnos = turnos; }


    public void agregarTurno(Turno turno) {
        turnos.add(turno);
    }


    public void quitarTurno(Turno turno) {
        turnos.remove(turno);
    }


    public List<Turno> getTurnosDeEmpleado(Empleado empleado) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos) {
            if (t.getEmpleado().equals(empleado)) {
                resultado.add(t);
            }
        }
        return resultado;
    }


    public List<Turno> getTurnosDia(String dia) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos) {
            if (t.getDia().equalsIgnoreCase(dia)) {
                resultado.add(t);
            }
        }
        return resultado;
    }


    public int contarMeserosEnDia(String dia) {
        int contador = 0;
        for (Turno t : getTurnosDia(dia)) {
            if (t.getEmpleado() instanceof Mesero) {
                contador++;
            }
        }
        return contador;
    }


    public int contarCocinerosEnDia(String dia) {
        int contador = 0;
        for (Turno t : getTurnosDia(dia)) {
            if ("cocinero".equalsIgnoreCase(t.getEmpleado().getTipo())) {
                contador++;
            }
        }
        return contador;
    }


    public boolean cumpleMinimosDia(String dia) {
        return contarMeserosEnDia(dia) >= 2 && contarCocinerosEnDia(dia) >= 1;
    }
}
