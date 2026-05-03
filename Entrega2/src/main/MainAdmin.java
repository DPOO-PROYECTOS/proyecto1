package main;
import logica.CafeLogica;
import modelo.Cafe;
import modelo.Admin;
import modelo.JuegoDeMesa;
import modelo.JuegoTablero;
import java.util.Scanner;
public class MainAdmin {
	public static void main(String[] args) {
		Scanner scanner= new Scanner(System.in);
		
		Cafe cafe = new Cafe("Uniandes Board", 100);
		CafeLogica logica= new CafeLogica(cafe);
		try {
		    System.out.println("Cargando base de datos...");
		    persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json");
		    System.out.println("Datos cargados correctamente.");
		} catch (Exception e) {
		    System.out.println("No se encontraron archivos previos o hubo un error. Se iniciará en blanco.");
		}
		if (cafe.getUsuarios().isEmpty()) {
			logica.registrarAdmin("admin", "1234");
			System.out.println("Se creó un usuario automatico");
			if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
			JuegoDeMesa juegoPrueba= new JuegoTablero("Catan", 1995,"Devir", 3, 4, false, false, "Bueno", true);
			logica.agregarJuegoInventarioVenta(juegoPrueba, 100000.0);
			
		}
		}
		System.out.println("MODULO ADMINISTRACION");
		
		Admin adminLogueado= null;
		while (adminLogueado== null){
			System.out.print("Ingrese usuario: ");
			String user= scanner.nextLine();
			System.out.print("Ingrese password: ");
			String pass = scanner.nextLine();
		
			try {
			adminLogueado= (Admin) logica.login(user, pass);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". Intente de nuevo.\n");
			}
		}
		boolean salir= false;
		while (!salir) {
			System.out.println("\n MENÚ PRINCIPAL ");
            System.out.println("1. Crear un Torneo");
            System.out.println("2. Ver catálogo de juegos");
            System.out.println("3. Salir y Guardar");
            System.out.print("Seleccione una opción: ");
            
			String opcion = scanner.nextLine();
			switch (opcion) {
				case "1":
					System.out.println("Creando Torneo");
					System.out.print("Ingrese dia de creacion de Torneo: ");
					String día= scanner.nextLine();
					
					System.out.print("Ingrese NOMBRE DEL JUEGO: ");
					String nombreJuego = scanner.nextLine();
					JuegoDeMesa juegoEncontrado= null;
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
							juegoEncontrado=j;
							break;
						}
					}
					if (juegoEncontrado==null) {
						System.out.println("Error, el juego no existe, juego:" + nombreJuego);
						break;
					}
					
					System.out.print("Ingrese cupos: ");
					String cuposStr= scanner.nextLine();
					int cupos=0;
					try {
						cupos=Integer.parseInt(cuposStr);
					} catch(NumberFormatException e) {
						System.out.println("Error, los cupos deben ser numero entero");
						break;
					}
					
					
					System.out.print("Ingrese tipo (Amistoso o Competitivo)");
					String tipo= scanner.nextLine();
					try {
						logica.crearTorneo(adminLogueado, juegoEncontrado, día, cupos, tipo);
					}catch(Exception e) {
						System.out.println("Error al crear el torneo: "+ e.getMessage());
					}
					break;
				case "2":
					System.out.println("Juegos Disponibles");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos papu :v");
					}else {
						for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
							System.out.println("-"+ j.getNombre()+" Precio: "+ j.getPrecioVenta());
						}
					}
					
					break;
				case "3":
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
					System.out.println("opcion invalidad, digite 1 2 o 3");	
			
			}
		
		}
		
		scanner.close();
		
		}
		
	}


