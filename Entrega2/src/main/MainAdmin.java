package main;
import logica.CafeLogica;
import modelo.Admin;
import modelo.Bebida;
import modelo.Cafe;
import modelo.JuegoDeMesa;
import modelo.JuegoTablero;
import modelo.Pasteleria;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class MainAdmin {
	public static void main(String[] args) {
		Scanner scanner= new Scanner(System.in);

		Cafe cafe = new Cafe("Uniandes Board", 100);
		CafeLogica logica= new CafeLogica(cafe);
		try {
		    System.out.println("Cargando base de datos...");
		    persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
		    System.out.println("Datos cargados correctamente.");
		} catch (Exception e) {
		    System.out.println("No se encontraron archivos previos o hubo un error. Se iniciará en blanco.");
		}
		if (cafe.getUsuarios().isEmpty()) {
			logica.registrarAdmin("admin", "1234");
			System.out.println("Se creó un admin por defecto");

			if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
			    JuegoDeMesa juegoVenta = new JuegoTablero("Catan", 1995, "Devir", 3, 4, false, false, "Nuevo", true);
			    logica.agregarJuegoInventarioVenta(juegoVenta, 100000.0);

			    JuegoDeMesa juegoPrestamo = new JuegoTablero("Ticket to Ride", 2004, "Days of Wonder", 2, 5, true, false, "Desgastado", true);
			    cafe.getInventarioPrestamo().agregarJuego(juegoPrestamo);
			}
		}
		System.out.println("MODULO ADMINISTRACION");

		Admin adminLogueado= null;
		while (adminLogueado== null) {
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
			System.out.println("\n=== MENÚ PRINCIPAL ADMIN ===");
            System.out.println("1. Crear un Torneo");
            System.out.println("2. Ver catálogo de juegos (Venta)");
            System.out.println("3. Ver catálogo de juegos (Préstamo)");
            System.out.println("4. Mover de venta a préstamo (se destapa)");
            System.out.println("5. Registrar empleado");
            System.out.println("6. Agregar item al menú (Bebida / Pastelería)");
            System.out.println("7. Gestionar solicitudes de cambio de turno");
            System.out.println("8. Salir y guardar");
            System.out.print("Seleccione una opción: ");

			String opcion = scanner.nextLine();
			switch (opcion) {
				case "1":
					System.out.println("\n--- Crear Torneo ---");
					System.out.print("Día de la semana del torneo: ");
					String día= scanner.nextLine();

					System.out.println("Juegos en inventario de préstamo:");
					for (JuegoDeMesa j : cafe.getInventarioPrestamo().getJuegos()) {
						System.out.println("  - " + j.getNombre());
					}
					System.out.print("Nombre del juego para el torneo: ");
					String nombreJuego = scanner.nextLine();
					JuegoDeMesa juegoEncontrado= null;
					for (JuegoDeMesa j: cafe.getInventarioPrestamo().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
							juegoEncontrado=j;
							break;
						}
					}
					if (juegoEncontrado==null) {
						System.out.println("Error: el juego '" + nombreJuego + "' no existe en el inventario de préstamo.");
						break;
					}

					System.out.print("Cupos máximos: ");
					String cuposStr= scanner.nextLine();
					int cupos=0;
					try {
						cupos=Integer.parseInt(cuposStr);
					} catch(NumberFormatException e) {
						System.out.println("Error: los cupos deben ser un número entero.");
						break;
					}

					System.out.print("Tipo (Amistoso o Competitivo): ");
					String tipo= scanner.nextLine();
					try {
						logica.crearTorneo(adminLogueado, juegoEncontrado, día, cupos, tipo);
						System.out.println("Torneo creado exitosamente.");
					} catch(Exception e) {
						System.out.println("Error al crear el torneo: "+ e.getMessage());
					}
					break;

				case "2":
					System.out.println("\n--- Catálogo de Venta ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en venta.");
					} else {
						for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
							System.out.println("  - " + j.getNombre() + " | Precio: $" + j.getPrecioVenta());
						}
					}
					break;

				case "3":
					System.out.println("\n--- Catálogo de Préstamo ---");
					if (cafe.getInventarioPrestamo().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en préstamo.");
					} else {
						for (JuegoDeMesa j: cafe.getInventarioPrestamo().getJuegos()) {
							String disp = j.isDisponible() ? "Disponible" : "Prestado";
							System.out.println("  - " + j.getNombre() + " [" + disp + "] | Veces prestado: " + j.getVecesPrestado());
						}
					}
					break;

				case "4":
					System.out.println("\n--- Mover juego de Venta a Préstamo ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en el inventario de venta.");
						break;
					}
					System.out.println("Juegos disponibles en vitrina:");
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						System.out.println("  - " + j.getNombre());
					}
					System.out.print("Nombre del juego a mover (destapar): ");
					String nombreMover= scanner.nextLine();

					JuegoDeMesa juegoAMover= null;
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						if(j.getNombre().equalsIgnoreCase(nombreMover)) {
							juegoAMover=j;
							break;
						}
					}
					if (juegoAMover== null) {
						System.out.println("El juego no existe en el inventario de venta.");
						break;
					}
					cafe.getInventarioVenta().getJuegos().remove(juegoAMover);
					juegoAMover.setEstado("Destapado/Usado");
					cafe.getInventarioPrestamo().getJuegos().add(juegoAMover);
					System.out.println("Juego '" + juegoAMover.getNombre() + "' movido a préstamo.");
					break;

				// ─────────────────────────────────────────────────────────────
				case "5":
					System.out.println("\n--- Registrar Empleado ---");
					System.out.println("Tipos disponibles:");
					System.out.println("  1. Mesero");
					System.out.println("  2. Cocinero");
					System.out.print("Seleccione tipo: ");
					String tipoOpc = scanner.nextLine();
					String tipoEmpleado;
					if (tipoOpc.equals("1")) {
						tipoEmpleado = "mesero";
					} else if (tipoOpc.equals("2")) {
						tipoEmpleado = "cocinero";
					} else {
						System.out.println("Tipo inválido.");
						break;
					}
					System.out.print("Login del nuevo empleado: ");
					String loginEmp = scanner.nextLine();
					System.out.print("Contraseña: ");
					String passEmp = scanner.nextLine();
					try {
						logica.registrarEmpleado(loginEmp, passEmp, tipoEmpleado);
						System.out.println("Empleado '" + loginEmp + "' (" + tipoEmpleado + ") registrado correctamente.");
					} catch (Exception e) {
						System.out.println("Error al registrar empleado: " + e.getMessage());
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "6":
					System.out.println("\n--- Agregar Item al Menú ---");
					System.out.println("  1. Bebida");
					System.out.println("  2. Pastelería");
					System.out.print("Tipo de item: ");
					String tipoItem = scanner.nextLine();

					System.out.print("Nombre: ");
					String nombreItem = scanner.nextLine();
					System.out.print("Precio: ");
					double precioItem;
					try {
						precioItem = Double.parseDouble(scanner.nextLine());
					} catch (NumberFormatException e) {
						System.out.println("Precio inválido.");
						break;
					}

					if (tipoItem.equals("1")) {
						System.out.print("¿Es alcohólica? (S/N): ");
						boolean esAlcoholica = scanner.nextLine().equalsIgnoreCase("S");
						System.out.print("¿Es caliente? (S/N): ");
						boolean esCaliente = scanner.nextLine().equalsIgnoreCase("S");
						Bebida bebida = new Bebida(nombreItem, precioItem, esAlcoholica, esCaliente);
						logica.agregarItemMenu(bebida);
						System.out.println("Bebida '" + nombreItem + "' agregada al menú.");

					} else if (tipoItem.equals("2")) {
						Pasteleria pasteleria = new Pasteleria(nombreItem, precioItem);
						System.out.print("¿Tiene alérgenos? (S/N): ");
						if (scanner.nextLine().equalsIgnoreCase("S")) {
							System.out.println("Ingrese alérgenos uno por uno (Enter vacío para terminar):");
							List<String> alergenos = new ArrayList<>();
							while (true) {
								System.out.print("  Alérgeno: ");
								String alergeno = scanner.nextLine().trim();
								if (alergeno.isEmpty()) break;
								pasteleria.agregarAlergeno(alergeno);
								alergenos.add(alergeno);
							}
							if (!alergenos.isEmpty()) {
								System.out.println("Alérgenos registrados: " + String.join(", ", alergenos));
							}
						}
						logica.agregarItemMenu(pasteleria);
						System.out.println("Pastelería '" + nombreItem + "' agregada al menú.");

					} else {
						System.out.println("Tipo de item inválido.");
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "7":

					System.out.println("\n--- GESTIÓN DE SOLICITUDES DE TURNO ---");
					
					java.util.List<modelo.SolicitudCambioTurno> pendientes = logica.getSolicitudesPendientes();
					
					if (pendientes.isEmpty()) {
					    System.out.println(" No hay solicitudes de cambio de turno pendientes para revisar.");
					    break;
					}

					System.out.println("Solicitudes pendientes:");
					for (int i = 0; i < pendientes.size(); i++) {
					    modelo.SolicitudCambioTurno sol = pendientes.get(i);
					    String nombreCompa = (sol.getIntercambiarCon() != null) ? sol.getIntercambiarCon().getLogin() : "Nadie (Dejar turno libre)";
					    
					    System.out.println((i + 1) + ". Solicitante: " + sol.getSolicitante().getLogin() + 
					                       " | Día: " + sol.getTurno().getDia() + 
					                       " | Reemplazo: " + nombreCompa);
					}

					System.out.print("\nIngrese el NÚMERO de la solicitud a gestionar (o 0 para cancelar): ");
					String numSolStr = scanner.nextLine();
					int numSol = 0;
					try {
					    numSol = Integer.parseInt(numSolStr);
					} catch (NumberFormatException e) {
					    System.out.println("Error: Debe ingresar un número entero.");
					    break;
					}

					if (numSol == 0 || numSol > pendientes.size()) {
					    System.out.println(" Operación cancelada.");
					    break;
					}

					modelo.SolicitudCambioTurno solicitudElegida = pendientes.get(numSol - 1);

					System.out.print("¿Desea (A)probar o (R)echazar esta solicitud?: ");
					String decision = scanner.nextLine();

					try {
					    if (decision.equalsIgnoreCase("A")) {
					        logica.aprobarCambioTurno(adminLogueado, solicitudElegida);
					        System.out.println("Solicitud aprobada con éxito. Turnos actualizados.");
					    } else if (decision.equalsIgnoreCase("R")) {
					        logica.rechazarCambioTurno(adminLogueado, solicitudElegida);
					        System.out.println(" Solicitud rechazada.");
					    } else {
					        System.out.println(" Opción inválida. No se realizó ninguna acción.");
					    }
					} catch (Exception e) {
					    System.out.println("Error al procesar la solicitud: " + e.getMessage());
					}
					break;
				case "8":
					salir= true;
					try {
					    System.out.println("\nGuardando datos en los archivos...");
					    persistencia.CentralPersistencia.guardarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
					} catch (Exception e) {
					    System.out.println("Error guardando los datos: " + e.getMessage());
					}
					System.out.println("Chao!");
					break;

				default:
					System.out.println("Opción inválida. Digite entre 1 y 8.");
			}
		}
		scanner.close();
	}
}

