package modelo;

public class Empleado extends Usuario {

    // ===== ATRIBUTOS =====
    private String tipo; // cocinero, mesero
    private boolean enTurno;
    private String codigoDescuento;



    // ===== CONSTRUCTOR =====
    public Empleado(String login, String password, String tipo) {
        super(login, password);
        this.tipo = tipo;
        this.enTurno = false;
        this.codigoDescuento = generarCodigo(login);
    }



    // ===== MÉTODOS =====
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isEnTurno() { return enTurno; }
    public void setEnTurno(boolean enTurno) { this.enTurno = enTurno; }

    public String getCodigoDescuento() { return codigoDescuento; }
    public void setCodigoDescuento(String codigoDescuento) { this.codigoDescuento = codigoDescuento; }


    private String generarCodigo(String login) {
        return "EMP-" + login.toUpperCase();
    }
}
