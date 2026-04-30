package main;

import modelo.*;
import logica.CafeLogica;
import persistencia.CentralPersistencia;

public class Main {

    public static void main(String[] args) {

        Cafe cafe = new Cafe("DulcesnDados", 50);
        CafeLogica logica = new CafeLogica(cafe);

        logica.registrarAdmin("admin", "1234");
        logica.registrarEmpleado("mesero1", "pass1", "mesero");
        logica.registrarEmpleado("mesero2", "pass2", "mesero");
        logica.registrarEmpleado("cocinero1", "pass3", "cocinero");
        logica.registrarCliente("cliente1", "c123");
        logica.registrarCliente("cliente2", "c123");

        logica.agregarMesa(new Mesa(1, 2));
        logica.agregarMesa(new Mesa(2, 2));
        logica.agregarMesa(new Mesa(3, 4));
        logica.agregarMesa(new Mesa(4, 6));

        JuegoDeMesa catan = new JuegoTablero("Catan", 1995, "Devir", 3, 4, false, false, "Excelente", true);
        logica.agregarJuegoInventarioPrestamo(catan);
        JuegoDeMesa jenga = new JuegoDeAccion("Jenga", 1983, "Hasbro", 2, 8, true, false, "Bueno", true);
        logica.agregarJuegoInventarioPrestamo(jenga);
        JuegoDeMesa gloomhaven = new JuegoDificil("Gloomhaven", 2017, "Cephalofair", 1, 4, false, true, "Nuevo", true);
        logica.agregarJuegoInventarioPrestamo(gloomhaven);
        
        JuegoDeMesa ticketToRide = new JuegoTablero("Ticket to Ride", 2004, "Days of Wonder", 2, 5, false, false, "Bueno", true);
        logica.agregarJuegoInventarioVenta(ticketToRide, 5.00);
        JuegoDeMesa operacion = new JuegoDeAccion("Operación", 1965, "Hasbro", 2, 6, true, false, "Regular", false);
        logica.agregarJuegoInventarioVenta(operacion, 8.65);
        JuegoDeMesa pandemicLegacy = new JuegoDificil("Pandemic Legacy", 2015, "Z-Man Games", 2, 4, false, true, "Nuevo", true);
        logica.agregarJuegoInventarioVenta(pandemicLegacy, 10.65);

        logica.agregarItemMenu(new Bebida("Cafe Latte", 4500, false, true));
        logica.agregarItemMenu(new Bebida("Cerveza Artesanal", 8000, true, false));
        logica.agregarItemMenu(new Bebida("Limonada", 3500, false, false));

        Pasteleria brownie = new Pasteleria("Brownie", 5000);
        brownie.agregarAlergeno("Nueces");
        brownie.agregarAlergeno("Gluten");
        logica.agregarItemMenu(brownie);

        try {
            CentralPersistencia.guardarTodo(cafe, "data/usuarios.json","data/inventarioPrestamos.json","data/menu.json","data/mesas.json", "data/inventarioVentas.json");

            System.out.println("Archivos generados correctamente");
        } catch (Exception e) {
            System.out.println("Error guardando: " + e.getMessage());
        }
    }
}