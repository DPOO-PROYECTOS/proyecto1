package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlanSemanal {

    // ===== ATRIBUTOS =====
    private List<Turno> turnos = new ArrayList<>();



    // ===== CONSTRUCTOR =====
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
        return turnos.stream()
                .filter(t -> t.getEmpleado().equals(empleado))
                .collect(Collectors.toList());
    }


    public List<Turno> getTurnosDia(String dia) {
        return turnos.stream()
                .filter(t -> t.getDia().equalsIgnoreCase(dia))
                .collect(Collectors.toList());
    }


    public long contarMeserosEnDia(String dia) {
        return getTurnosDia(dia).stream()
                .filter(t -> t.getEmpleado() instanceof Mesero)
                .count();
    }


    public long contarCocinerosEnDia(String dia) {
        return getTurnosDia(dia).stream()
                .filter(t -> "cocinero".equalsIgnoreCase(t.getEmpleado().getTipo()))
                .count();
    }


    public boolean cumpleMinimosDia(String dia) {
        return contarMeserosEnDia(dia) >= 2 && contarCocinerosEnDia(dia) >= 1;
    }
}
