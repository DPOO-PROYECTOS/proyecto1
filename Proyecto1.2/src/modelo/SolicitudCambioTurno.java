package modelo;

public class SolicitudCambioTurno {

    // ===== ATRIBUTOS =====
    private Turno turno;
    private Empleado solicitante;
    private Empleado intercambiarCon;
    private Admin aprobadaPor;
    private boolean aprobada;
    private String estado; // pendiente, aprobada, rechazada



    // ===== CONSTRUCTOR =====
    public SolicitudCambioTurno(Turno turno, Empleado solicitante, Empleado intercambiarCon) {
        this.turno = turno;
        this.solicitante = solicitante;
        this.intercambiarCon = intercambiarCon;
        this.aprobada = false;
        this.estado = "pendiente";
    }



    // ===== MÉTODOS =====
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public Empleado getSolicitante() { return solicitante; }
    public void setSolicitante(Empleado solicitante) { this.solicitante = solicitante; }

    public Empleado getIntercambiarCon() { return intercambiarCon; }
    public void setIntercambiarCon(Empleado intercambiarCon) { this.intercambiarCon = intercambiarCon; }

    public Admin getAprobadaPor() { return aprobadaPor; }
    public void setAprobadaPor(Admin aprobadaPor) { this.aprobadaPor = aprobadaPor; }

    public boolean isAprobada() { return aprobada; }
    public void setAprobada(boolean aprobada) { this.aprobada = aprobada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }


    public void aprobar(Admin admin) {
        this.aprobadaPor = admin;
        this.aprobada = true;
        this.estado = "aprobada";
    }


    public void rechazar(Admin admin) {
        this.aprobadaPor = admin;
        this.aprobada = false;
        this.estado = "rechazada";
    }
}
