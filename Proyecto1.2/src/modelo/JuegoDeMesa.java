package modelo;

public abstract class JuegoDeMesa {

    // Attributes
    private String nombre;
    private int anioPublicacion;
    private String empresaMatriz;
    private int minJugadores;
    private int maxJugadores;
    private boolean aptaMenores5;
    private boolean soloAdultos;
    private String estado;
    private boolean disponible;
    private int vecesPrestado;
    private double precioVenta;



    // Constructor
    protected JuegoDeMesa(String nombre, int anioPublicacion, String empresaMatriz,
                          int minJugadores, int maxJugadores, boolean aptaMenores5,
                          boolean soloAdultos, String estado, boolean disponible) {
        this.nombre = nombre;
        this.anioPublicacion = anioPublicacion;
        this.empresaMatriz = empresaMatriz;
        this.minJugadores = minJugadores;
        this.maxJugadores = maxJugadores;
        this.aptaMenores5 = aptaMenores5;
        this.soloAdultos = soloAdultos;
        this.estado = estado;
        this.disponible = disponible;
        this.vecesPrestado = 0;
    }



    // ===== MÉTODOS =====
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getEmpresaMatriz() { return empresaMatriz; }
    public void setEmpresaMatriz(String empresaMatriz) { this.empresaMatriz = empresaMatriz; }

    public int getMinJugadores() { return minJugadores; }
    public void setMinJugadores(int minJugadores) { this.minJugadores = minJugadores; }

    public int getMaxJugadores() { return maxJugadores; }
    public void setMaxJugadores(int maxJugadores) { this.maxJugadores = maxJugadores; }

    public boolean isAptaMenores5() { return aptaMenores5; }
    public void setAptaMenores5(boolean aptaMenores5) { this.aptaMenores5 = aptaMenores5; }

    public boolean isSoloAdultos() { return soloAdultos; }
    public void setSoloAdultos(boolean soloAdultos) { this.soloAdultos = soloAdultos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getVecesPrestado() { return vecesPrestado; }
    public void setVecesPrestado(int vecesPrestado) { this.vecesPrestado = vecesPrestado; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }


    public void incrementarPrestamos() {
        this.vecesPrestado++;
    }


    public boolean soportaNumJugadores(int numJugadores) {
        return numJugadores >= minJugadores && numJugadores <= maxJugadores;
    }


    public boolean esAptoParaMesa(int numPersonas, boolean tieneNinos, boolean tieneJovenes) {
        if (!soportaNumJugadores(numPersonas)) return false;
        if (soloAdultos && (tieneNinos || tieneJovenes)) return false;
        return true;
    }
}
