package main;

import modelo.*;
import logica.CafeLogica;
import persistencia.CentralPersistencia;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        Cafe cafe = new Cafe("DulcesnDados", 50);
        CafeLogica logica = new CafeLogica(cafe);

        File carpeta = new File("data");
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        String pathU = "./data/usuarios.json";
        String pathI = "./data/inventario.json";
        String pathM = "./data/menu.json";
        String pathMe = "./data/mesas.json";

        File archivoPrueba = new File(pathU);
        if (archivoPrueba.exists()) {
            System.out.println("Probando carga");
            try {
                CentralPersistencia.cargarTodo(cafe, pathU, pathI, pathM, pathMe);
                System.out.println("exitoso. Usuarios cargados: " + cafe.getUsuarios().size());
            } catch (Exception e) {
                System.out.println("ERROR" + e.getMessage());
            }
        } else {
            System.out.println("No se encuentran archivos, se inicializan datos por defecto");
            inicializarDatos(logica);
        }

        System.out.println("Ejecuta lógica del negocio");
        System.out.println("Estado actual del inventario: " + cafe.getInventarioPrestamo().getJuegos().size() + " juegos.");

        System.out.println("Intento guardar cambios");
        try {
            CentralPersistencia.guardarTodo(cafe, pathU, pathI, pathM, pathMe);
            System.out.println("Se guardó exitosamente en en la carpeta /data");
        } catch (Exception e) {
            System.out.println("error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inicializarDatos(CafeLogica logica) {
        logica.registrarAdmin("admin", "1234");
        logica.registrarEmpleado("mesero1", "pass1", "mesero");
        logica.agregarMesa(new Mesa(1, 2));
        logica.agregarMesa(new Mesa(2, 4));
        logica.agregarJuegoInventarioPrestamo(new JuegoTablero("Catan", 1995, "Devir", 3, 4, false, false, "Excelente", true));
        logica.agregarItemMenu(new Bebida("Cafe Latte", 4500, false, true));
    }
    
    
    
    
    
}