package modelo;

import java.util.ArrayList;
import java.util.List;

public class Mesa {

    // Attributes
    private int numero;
    private int capacidad;
    private int numPersonas;
    private boolean tieneNinos;
    private boolean tieneJovenes;
    private Cliente clienteAsignado;
    private List<Bebida> bebidas = new ArrayList<>();



    // Constructor
    public Mesa(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
    }



    // ===== MÉTODOS =====
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public int getNumPersonas() { return numPersonas; }
    public void setNumPersonas(int numPersonas) { this.numPersonas = numPersonas; }

    public boolean isTieneNinos() { return tieneNinos; }
    public void setTieneNinos(boolean tieneNinos) { this.tieneNinos = tieneNinos; }

    public boolean isTieneJovenes() { return tieneJovenes; }
    public void setTieneJovenes(boolean tieneJovenes) { this.tieneJovenes = tieneJovenes; }

    public Cliente getClienteAsignado() { return clienteAsignado; }
    public void setClienteAsignado(Cliente clienteAsignado) { this.clienteAsignado = clienteAsignado; }

    public List<Bebida> getBebidas() { return bebidas; }
    public void setBebidas(List<Bebida> bebidas) { this.bebidas = bebidas; }


    public boolean estaDisponible() {
        return clienteAsignado == null;
    }


    public boolean tieneCapacidadPara(int personas) {
        return personas <= capacidad;
    }


    public void agregarBebida(Bebida bebida) {
        bebidas.add(bebida);
    }


    public boolean tieneBebidaCaliente() {
        for (Bebida bebida : bebidas) {
            if (bebida.isEsCaliente()) {
                return true;
            }
        }
        return false;
    }


    public boolean tieneMenores() {
        return tieneNinos || tieneJovenes;
    }


    public void liberar() {
        this.clienteAsignado = null;
        this.numPersonas = 0;
        this.tieneNinos = false;
        this.tieneJovenes = false;
        this.bebidas.clear();
    }
}
