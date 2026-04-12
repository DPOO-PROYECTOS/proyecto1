package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pasteleria extends ItemMenu {

    // ===== ATRIBUTOS =====
    private List<String> alergenos = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    public Pasteleria(String nombre, double precio) {
        super(nombre, precio);
    }



    // ===== MÉTODOS =====
    public List<String> getAlergenos() { return alergenos; }
    public void setAlergenos(List<String> alergenos) { this.alergenos = alergenos; }


    public void agregarAlergeno(String alergeno) {
        if (!alergenos.contains(alergeno)) {
            alergenos.add(alergeno);
        }
    }


    public void quitarAlergeno(String alergeno) {
        alergenos.remove(alergeno);
    }


    public boolean tieneAlergeno(String alergeno) {
        return alergenos.contains(alergeno);
    }
}
