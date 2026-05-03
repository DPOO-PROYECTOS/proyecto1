package main;

import logica.CafeLogica;
import modelo.Cafe;
import modelo.Cliente;
import modelo.JuegoDeMesa;
import modelo.Torneo;
import modelo.VentaJuego;

import java.util.ArrayList;
import java.util.Scanner;
public class MainCliente {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Cafe cafe= new Cafe("Uniandes board", 100);
		CafeLogica logica= new CafeLogica(cafe);
		
		try {
            System.out.println("Cargando base de datos del Café...");
            persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json");
        } catch (Exception e) {
            System.out.println("Iniciando sistema en blanco (No se encontraron archivos).");
        }
		
		System.out.println("BIENVENIDOOOOOOOOOOOOOOOOOOOOOOOOOO");
		
		Cliente clienteLogueado=null;
		while (clienteLogueado== null) {
			System.out.println("\n1. Iniciar Sesion");
			System.out.println("2. Registrarse como nuevo cliente");
			System.out.print("Elija una opción: ");
			String opcLogin= scanner.nextLine();
			
			if(opcLogin.equals("1")) {
				System.out.print("Ingrese Usuario");
				String user= scanner.nextLine();
				System.out.print("Ingrese contraseña");
				String pass= scanner.nextLine();
				try {
					clienteLogueado= (Cliente) logica.login(user, pass);
					System.out.println("Hola de nuevo "+ user);
				} catch(Exception e){
					System.out.println("Error "+ e.getMessage());
				}
			}else if (opcLogin.equals("2")) {
				System.out.print("cree un nuevo usuario: ");
				String newUser = scanner.nextLine();
				System.out.print("cree una contraseña: ");
				String newPass= scanner.nextLine();
				
				try {
					clienteLogueado=logica.registrarCliente(newUser, newPass);
					System.out.println("Bienvenido al sistema");
				}catch (Exception e) {
					System.out.println("Lo sentimos, error: " + e.getMessage());
				}
				
				
			} else {
				System.out.println("Opción inválida");
				
			}
			
			
			
		}
		boolean salir= false; //guarda
		
		while (!salir) {
			System.out.println("Menu cliente");
			System.out.println("1. Ver catálogo de juegos (Préstamo y Venta)");
            System.out.println("2. Inscribirse a un Torneo");
            System.out.println("3. Comprar un juego (Inventario Venta)");
            System.out.println("4. Salir y Guardar");
            System.out.print("Seleccione una opción: ");
            
			String opcion= scanner.nextLine();
			
			switch (opcion) {
				case"1":
					System.out.println("Haz elegido opción 1");
					System.out.println("Catálogo de Préstamo: ");
					for (JuegoDeMesa j: cafe.getInventarioPrestamo().getJuegos()) {
						System.out.println("- "+ j.getNombre());
					}
					System.out.println("Catálogo de Venta: ");
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						System.out.println("- "+ j.getNombre() + "Precio: " + j.getPrecioVenta());
					}
					break;
					
					
				case "2":
					System.out.println("Mostrando torneos disponibles...");
					for (Torneo t: cafe.getTorneos()) {
						System.out.println("- Juego: " + t.getJuego().getNombre() + 
			                       " | Día: " + t.getDiaSemana() + 
			                       " | Tipo: " + t.getClass().getSimpleName() + 
			                       " | Cupos Libres: " + t.getCuposDisponibles());
					}
					System.out.print("Ingrese el nombre del JUEGO del torneo al que desea ingresar: ");
					
					String nombreTorneo= scanner.nextLine();
					
					Torneo torneoElegido= null;
					for (Torneo t: cafe.getTorneos()) {
						if (t.getJuego().getNombre().equalsIgnoreCase(nombreTorneo)) {
							torneoElegido= t;
							break;
						}
					}
					
					if (torneoElegido== null) {
						System.out.println("No se encontró torneo activo");
						break;
					}
					
					System.out.print("Cuantos cupos desea reservar: ");
					String cupos= scanner.nextLine();
					int cuposReserva=0;
					try {
					    cuposReserva = Integer.parseInt(cupos);
					} catch (NumberFormatException e) {
					    System.out.println(" Debe ingresar un número entero.");
					    break;
					}
					try {
						logica.inscribirEnTorneo(clienteLogueado, torneoElegido, cuposReserva);
						System.out.println("RegistroExistoso");
					}catch(Exception e) {
						System.out.println("Error en la inscripción: "+ e.getMessage());
					}
					
					break;
					
				
				case "3":
					System.out.print("ingrese el nombre del juego que desea comprar: ");
					String nombreJuego= scanner.nextLine();
					JuegoDeMesa juegoAComprar=null;
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
							juegoAComprar=j;
							break;
						}
					}
					if (juegoAComprar==null) {
						System.out.println("El juego no está en el inventario de ventas");
						break;
					}
					System.out.print("Tienes algun descuento de empleado? (Presiona Enter si no)");
					String codigoDesc= scanner.nextLine();
					System.out.print("¿Tienes un bono de ganador de torneo para aplicar? (S/N): ");
					String respBono = scanner.nextLine();
					boolean usarBonoTorneo = respBono.equalsIgnoreCase("S");
					System.out.print("Cuantos puntos de fidelidad deseas usar? Tienes" + clienteLogueado.getPuntosFidelidad()+"Puntos ");
					String puntosUs= scanner.nextLine();
					int puntosUsar=0;
					if (!puntosUs.isEmpty()) {
					    try {
					        puntosUsar = Integer.parseInt(puntosUs);
					    } catch (NumberFormatException e) {
					        System.out.println("Error: Número de puntos inválido. Se usarán 0 puntos por defecto.");
					    }
					}
					try {
						ArrayList<JuegoDeMesa> juegosComprar= new ArrayList<JuegoDeMesa>();
						juegosComprar.add(juegoAComprar);
						VentaJuego venta= logica.venderJuegos(clienteLogueado, juegosComprar, codigoDesc, puntosUsar, usarBonoTorneo);
						System.out.println("Total pagado (con IVA y descuentos): $" + venta.calcularTotal());
					    System.out.println("Tus puntos de fidelidad actuales: " + clienteLogueado.getPuntosFidelidad());
					    
					    
					}catch(Exception e) {
						System.out.println("Error al procesar compra "+e.getMessage());
					}
							
					break;
				
				case "4":
					salir= true;
					try {
					    System.out.println("\nGuardando datos en los archivos...");
					    persistencia.CentralPersistencia.guardarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json");
					} catch (Exception e) {
					    System.out.println("Error guardando los datos: " + e.getMessage());
					}
					System.out.println("Chao");
					break;
				default:
					System.out.println("Opcion invalida. Digite 1, 2, 3 o 4");
				
			
			}
		}
		scanner.close();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
