package modelo;

public class Turno {

    // Attributes
    private String dia;
    private String horaInicio;
    private String horaFin;
    private Empleado empleado;



    // Constructor
    public Turno(String dia, String horaInicio, String horaFin, Empleado empleado) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.empleado = empleado;
    }



    // ===== MÉTODOS =====
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
