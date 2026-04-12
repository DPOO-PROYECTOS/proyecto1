package modelo;

import java.time.LocalDateTime;

public class PrestamoEmpleado extends Prestamo {

    // ===== ATRIBUTOS =====
    private Empleado empleado;



    // ===== CONSTRUCTOR =====
    public PrestamoEmpleado(LocalDateTime fechaInicio, Empleado empleado) {
        super(fechaInicio);
        this.empleado = empleado;
    }



    // ===== MÉTODOS =====
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
