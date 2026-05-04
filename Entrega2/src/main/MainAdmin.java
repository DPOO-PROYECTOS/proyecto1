package main;

import logica.CafeLogica;
import modelo.Admin;
import modelo.Bebida;
import modelo.Cafe;
import modelo.Empleado;
import modelo.JuegoDeMesa;
import modelo.JuegoTablero;
import modelo.JuegoDeCartas;
import modelo.JuegoDeAccion;
import modelo.JuegoDificil;
import modelo.Pasteleria;
import modelo.Prestamo;
import modelo.PrestamoCliente;
import modelo.SugerenciaPlatillo;
import modelo.Torneo;
import modelo.TorneoCompetitivo;
import modelo.Turno;
import modelo.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainAdmin {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		Cafe cafe = new Cafe("Uniandes Board", 100);
		CafeLogica logica = new CafeLogica(cafe);
		try {
			System.out.println("Cargando base de datos...");
			persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
			System.out.println("Datos cargados correctamente.");
		} catch (Exception e) {
			System.out.println("No se encontraron archivos previos o hubo un error. Se iniciará en blanco.");
		}

		if (cafe.getUsuarios().isEmpty()) {
			logica.registrarAdmin("admin", "1234");
			System.out.println("Se creó un admin por defecto: usuario=admin, password=1234");

			if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
				JuegoDeMesa juegoVenta = new JuegoTablero("Catan", 1995, "Devir", 3, 4, false, false, "Nuevo", true);
				logica.agregarJuegoInventarioVenta(juegoVenta, 100000.0);
				JuegoDeMesa juegoPrestamo = new JuegoTablero("Ticket to Ride", 2004, "Days of Wonder", 2, 5, true, false, "Desgastado", true);
				cafe.getInventarioPrestamo().agregarJuego(juegoPrestamo);
			}
		}

		System.out.println("MODULO ADMINISTRACION");

		Admin adminLogueado = null;
		while (adminLogueado == null) {
			System.out.print("Ingrese usuario: ");
			String user = scanner.nextLine();
			System.out.print("Ingrese password: ");
			String pass = scanner.nextLine();
			try {
				adminLogueado = (Admin) logica.login(user, pass);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + ". Intente de nuevo.\n");
			}
		}

		boolean salir = false;
		while (!salir) {
			System.out.println("\n=== MENÚ PRINCIPAL ADMIN ===");
			System.out.println("1.  Crear un Torneo");
			System.out.println("2.  Ver catálogo de juegos (Venta)");
			System.out.println("3.  Ver catálogo de juegos (Préstamo)");
			System.out.println("4.  Mover de venta a préstamo (se destapa)");
			System.out.println("5.  Registrar empleado");
			System.out.println("6.  Agregar item al menú (Bebida / Pastelería)");
			System.out.println("7.  Gestionar solicitudes de cambio de turno");
			System.out.println("8.  Ver historial de préstamos");
			System.out.println("9.  Gestionar inventario (agregar / reparar / marcar robado)");
			System.out.println("10. Gestionar turnos de empleados");
			System.out.println("11. Ver informes de ventas");
			System.out.println("12. Aprobar/rechazar sugerencias de platillos");
			System.out.println("13. Premiar ganador de torneo");
			System.out.println("14. Salir y guardar");
			System.out.print("Seleccione una opción: ");

			String opcion = scanner.nextLine();
			switch (opcion) {

				// CREAR TORNEO
				case "1":
					System.out.println("\n--- Crear Torneo ---");
					System.out.print("Día de la semana del torneo: ");
					String dia = scanner.nextLine();

					System.out.println("Juegos en inventario de préstamo:");
					for (JuegoDeMesa j : cafe.getInventarioPrestamo().getJuegos()) {
						System.out.println("  - " + j.getNombre());
					}
					System.out.print("Nombre del juego para el torneo: ");
					String nombreJuego = scanner.nextLine();
					JuegoDeMesa juegoEncontrado = null;
					for (JuegoDeMesa j : cafe.getInventarioPrestamo().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
							juegoEncontrado = j;
							break;
						}
					}
					if (juegoEncontrado == null) {
						System.out.println("Error: el juego '" + nombreJuego + "' no existe en el inventario de préstamo.");
						break;
					}
					System.out.print("Cupos máximos: ");
					int cupos = 0;
					try {
						cupos = Integer.parseInt(scanner.nextLine());
					} catch (NumberFormatException e) {
						System.out.println("Error: los cupos deben ser un número entero.");
						break;
					}
					System.out.print("Tipo (Amistoso o Competitivo): ");
					String tipo = scanner.nextLine();
					try {
						logica.crearTorneo(adminLogueado, juegoEncontrado, dia, cupos, tipo);
						System.out.println("Torneo creado exitosamente.");
					} catch (Exception e) {
						System.out.println("Error al crear el torneo: " + e.getMessage());
					}
					break;

				// CATÁLOGO VENTA
				case "2":
					System.out.println("\n--- Catálogo de Venta ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en venta.");
					} else {
						for (JuegoDeMesa j : cafe.getInventarioVenta().getJuegos()) {
							System.out.println("  - " + j.getNombre() + " | Precio: $" + j.getPrecioVenta() + " | Estado: " + j.getEstado());
						}
					}
					break;

				// CATÁLOGO PRÉSTAMO
				case "3":
					System.out.println("\n--- Catálogo de Préstamo ---");
					if (cafe.getInventarioPrestamo().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en préstamo.");
					} else {
						for (JuegoDeMesa j : cafe.getInventarioPrestamo().getJuegos()) {
							String disp = j.isDisponible() ? "Disponible" : "Prestado";
							System.out.println("  - " + j.getNombre() + " [" + disp + "] | Veces prestado: " + j.getVecesPrestado() + " | Estado: " + j.getEstado());
						}
					}
					break;

				// MOVER VENTA A PRESTAMO 
				case "4":
					System.out.println("\n--- Mover juego de Venta a Préstamo ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos en el inventario de venta.");
						break;
					}
					System.out.println("Juegos disponibles en vitrina:");
					for (JuegoDeMesa j : cafe.getInventarioVenta().getJuegos()) {
						System.out.println("  - " + j.getNombre());
					}
					System.out.print("Nombre del juego a mover (destapar): ");
					String nombreMover = scanner.nextLine();
					JuegoDeMesa juegoAMover = null;
					for (JuegoDeMesa j : cafe.getInventarioVenta().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreMover)) {
							juegoAMover = j;
							break;
						}
					}
					if (juegoAMover == null) {
						System.out.println("El juego no existe en el inventario de venta.");
						break;
					}
					cafe.getInventarioVenta().getJuegos().remove(juegoAMover);
					juegoAMover.setEstado("Destapado/Usado");
					cafe.getInventarioPrestamo().getJuegos().add(juegoAMover);
					System.out.println("Juego '" + juegoAMover.getNombre() + "' movido a préstamo.");
					break;

				// REGISTRAR EMPLEADO
				case "5":
					System.out.println("\n--- Registrar Empleado ---");
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

				// AGREGAR ITEM MENU
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
						logica.agregarItemMenu(new Bebida(nombreItem, precioItem, esAlcoholica, esCaliente));
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

				// SOLICITUDES CAMBIO TURNO
				case "7":
					System.out.println("\n--- GESTIÓN DE SOLICITUDES DE TURNO ---");
					List<modelo.SolicitudCambioTurno> pendientes = logica.getSolicitudesPendientes();
					if (pendientes.isEmpty()) {
						System.out.println("No hay solicitudes de cambio de turno pendientes.");
						break;
					}
					System.out.println("Solicitudes pendientes:");
					for (int i = 0; i < pendientes.size(); i++) {
						modelo.SolicitudCambioTurno sol = pendientes.get(i);
						String nombreCompa = (sol.getIntercambiarCon() != null) ? sol.getIntercambiarCon().getLogin() : "Nadie (dejar libre)";
						System.out.println((i + 1) + ". Solicitante: " + sol.getSolicitante().getLogin() +
								" | Día: " + sol.getTurno().getDia() +
								" | Reemplazo: " + nombreCompa);
					}
					System.out.print("\nNúmero de solicitud a gestionar (0 para cancelar): ");
					int numSol = 0;
					try {
						numSol = Integer.parseInt(scanner.nextLine());
					} catch (NumberFormatException e) {
						System.out.println("Error: ingrese un número entero.");
						break;
					}
					if (numSol == 0 || numSol > pendientes.size()) {
						System.out.println("Operación cancelada.");
						break;
					}
					modelo.SolicitudCambioTurno solicitudElegida = pendientes.get(numSol - 1);
					System.out.print("¿Desea (A)probar o (R)echazar esta solicitud?: ");
					String decision = scanner.nextLine();
					try {
						if (decision.equalsIgnoreCase("A")) {
							logica.aprobarCambioTurno(adminLogueado, solicitudElegida);
							System.out.println("Solicitud aprobada. Turnos actualizados.");
						} else if (decision.equalsIgnoreCase("R")) {
							logica.rechazarCambioTurno(adminLogueado, solicitudElegida);
							System.out.println("Solicitud rechazada.");
						} else {
							System.out.println("Opción inválida. No se realizó ninguna acción.");
						}
					} catch (Exception e) {
						System.out.println("Error al procesar la solicitud: " + e.getMessage());
					}
					break;

				// HISTORIAL DE PRÉSTAMOS
				case "8":
					System.out.println("\n--- Historial completo de Préstamos ---");
					List<Prestamo> historial = cafe.getHistorialPrestamos();
					if (historial.isEmpty()) {
						System.out.println("No hay préstamos registrados.");
					} else {
						for (int i = 0; i < historial.size(); i++) {
							Prestamo p = historial.get(i);
							String usuario;
							if (p instanceof PrestamoCliente) {
								usuario = "Cliente: " + ((PrestamoCliente) p).getCliente().getLogin();
							} else {
								usuario = "Empleado: " + ((modelo.PrestamoEmpleado) p).getEmpleado().getLogin();
							}
							String estado = p.estaActivo() ? "Activo" : "Devuelto";
							System.out.println((i + 1) + ". " + usuario + " | Estado: " + estado);
							for (JuegoDeMesa j : p.getJuegos()) {
								System.out.println("     - " + j.getNombre() + " [" + j.getEstado() + "] | Veces prestado: " + j.getVecesPrestado());
							}
						}
					}
					break;

				// GESTIONAR INVENTARIO
				case "9":
					System.out.println("\n--- Gestión de Inventario ---");
					System.out.println("  1. Agregar juego al inventario de Venta");
					System.out.println("  2. Agregar juego al inventario de Préstamo");
					System.out.println("  3. Reparar un juego (reemplazar copia dañada)");
					System.out.println("  4. Marcar juego como robado/desaparecido");
					System.out.print("Opción: ");
					String subOpc9 = scanner.nextLine();

					if (subOpc9.equals("1") || subOpc9.equals("2")) {
						System.out.println("Tipo de juego:");
						System.out.println("  1. Tablero  2. Cartas  3. Acción  4. Difícil");
						System.out.print("Tipo: ");
						String tipoJuego = scanner.nextLine();
						System.out.print("Nombre: ");
						String njNombre = scanner.nextLine();
						System.out.print("Año de publicación: ");
						int njAnio = 0;
						try { njAnio = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Año inválido."); break; }
						System.out.print("Empresa matriz: ");
						String njEmpresa = scanner.nextLine();
						System.out.print("Mínimo de jugadores: ");
						int njMin = 0;
						try { njMin = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						System.out.print("Máximo de jugadores: ");
						int njMax = 0;
						try { njMax = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						System.out.print("¿Apto para menores de 5 años? (S/N): ");
						boolean aptaMenores = scanner.nextLine().equalsIgnoreCase("S");
						System.out.print("¿Solo adultos? (S/N): ");
						boolean soloAdultos = scanner.nextLine().equalsIgnoreCase("S");
						System.out.print("Estado (Nuevo/Bueno/Desgastado): ");
						String njEstado = scanner.nextLine();

						JuegoDeMesa nuevoJuego;
						switch (tipoJuego) {
							case "2":  nuevoJuego = new JuegoDeCartas(njNombre, njAnio, njEmpresa, njMin, njMax, aptaMenores, soloAdultos, njEstado, true); break;
							case "3":  nuevoJuego = new JuegoDeAccion(njNombre, njAnio, njEmpresa, njMin, njMax, aptaMenores, soloAdultos, njEstado, true); break;
							case "4":  nuevoJuego = new JuegoDificil(njNombre, njAnio, njEmpresa, njMin, njMax, aptaMenores, soloAdultos, njEstado, true); break;
							default:   nuevoJuego = new JuegoTablero(njNombre, njAnio, njEmpresa, njMin, njMax, aptaMenores, soloAdultos, njEstado, true); break;
						}

						if (subOpc9.equals("1")) {
							System.out.print("Precio de venta: ");
							double njPrecio = 0;
							try { njPrecio = Double.parseDouble(scanner.nextLine()); }
							catch (NumberFormatException e) { System.out.println("Precio inválido."); break; }
							logica.agregarJuegoInventarioVenta(nuevoJuego, njPrecio);
							System.out.println("Juego '" + njNombre + "' agregado al inventario de venta.");
						} else {
							logica.agregarJuegoInventarioPrestamo(nuevoJuego);
							System.out.println("Juego '" + njNombre + "' agregado al inventario de préstamo.");
						}

					} else if (subOpc9.equals("3")) {
						System.out.println("Juegos en préstamo:");
						List<JuegoDeMesa> jprestamo = cafe.getInventarioPrestamo().getJuegos();
						if (jprestamo.isEmpty()) { System.out.println("No hay juegos en préstamo."); break; }
						for (int i = 0; i < jprestamo.size(); i++) {
							System.out.println("  " + (i + 1) + ". " + jprestamo.get(i).getNombre() + " [" + jprestamo.get(i).getEstado() + "]");
						}
						System.out.print("Número del juego a reparar: ");
						int numRep = 0;
						try { numRep = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						if (numRep < 1 || numRep > jprestamo.size()) { System.out.println("Opción fuera de rango."); break; }
						try {
							logica.repararJuego(jprestamo.get(numRep - 1));
							System.out.println("Juego reparado con éxito (copia de venta usada como reemplazo).");
						} catch (Exception e) {
							System.out.println("Error al reparar: " + e.getMessage());
						}

					} else if (subOpc9.equals("4")) {
						System.out.println("Juegos en préstamo:");
						List<JuegoDeMesa> jprestamo = cafe.getInventarioPrestamo().getJuegos();
						if (jprestamo.isEmpty()) { System.out.println("No hay juegos en préstamo."); break; }
						for (int i = 0; i < jprestamo.size(); i++) {
							System.out.println("  " + (i + 1) + ". " + jprestamo.get(i).getNombre());
						}
						System.out.print("Número del juego a marcar como robado: ");
						int numRob = 0;
						try { numRob = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						if (numRob < 1 || numRob > jprestamo.size()) { System.out.println("Opción fuera de rango."); break; }
						logica.marcarComoRobado(jprestamo.get(numRob - 1));
						System.out.println("Juego marcado como desaparecido y retirado del inventario.");
					} else {
						System.out.println("Opción inválida.");
					}
					break;

				// GESTIONAR TURNOS
				case "10":
					System.out.println("\n--- Gestión de Turnos de Empleados ---");
					System.out.println("  1. Crear turno para un empleado");
					System.out.println("  2. Ver todos los turnos del plan semanal");
					System.out.print("Opción: ");
					String subOpc10 = scanner.nextLine();

					if (subOpc10.equals("1")) {
						List<Usuario> usuarios = cafe.getUsuarios();
						List<Empleado> empleados = new ArrayList<>();
						for (Usuario u : usuarios) {
							if (u instanceof Empleado) empleados.add((Empleado) u);
						}
						if (empleados.isEmpty()) { System.out.println("No hay empleados registrados."); break; }
						System.out.println("Empleados disponibles:");
						for (int i = 0; i < empleados.size(); i++) {
							System.out.println("  " + (i + 1) + ". " + empleados.get(i).getLogin() + " (" + empleados.get(i).getTipo() + ")");
						}
						System.out.print("Número del empleado: ");
						int numEmp = 0;
						try { numEmp = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						if (numEmp < 1 || numEmp > empleados.size()) { System.out.println("Opción fuera de rango."); break; }
						Empleado empElegido = empleados.get(numEmp - 1);
						System.out.print("Día de la semana (ej: Lunes): ");
						String diaTurno = scanner.nextLine();
						System.out.print("Hora de inicio (ej: 08:00): ");
						String horaInicio = scanner.nextLine();
						System.out.print("Hora de fin (ej: 16:00): ");
						String horaFin = scanner.nextLine();
						try {
							logica.crearTurno(empElegido, diaTurno, horaInicio, horaFin);
							System.out.println("Turno creado para '" + empElegido.getLogin() + "' el " + diaTurno + " de " + horaInicio + " a " + horaFin + ".");
						} catch (Exception e) {
							System.out.println("Error al crear turno: " + e.getMessage());
						}

					} else if (subOpc10.equals("2")) {
						List<Turno> todosTurnos = cafe.getPlanSemanal().getTurnos();
						if (todosTurnos.isEmpty()) {
							System.out.println("No hay turnos programados.");
						} else {
							System.out.println("Plan semanal:");
							for (Turno t : todosTurnos) {
								System.out.println("  - " + t.getEmpleado().getLogin() + " (" + t.getEmpleado().getTipo() + ")" +
										" | Día: " + t.getDia() + " | " + t.getHoraInicio() + " - " + t.getHoraFin());
							}
						}
					} else {
						System.out.println("Opción inválida.");
					}
					break;

				// INFORMES DE VENTAS
				case "11":
					System.out.println("\n--- Informes de Ventas ---");
					System.out.println("  1. Informe del día");
					System.out.println("  2. Informe semanal (desde una fecha)");
					System.out.println("  3. Informe mensual");
					System.out.print("Opción: ");
					String subOpc11 = scanner.nextLine();

					if (subOpc11.equals("1")) {
						System.out.print("Fecha (YYYY-MM-DD): ");
						LocalDate fecha = null;
						try { fecha = LocalDate.parse(scanner.nextLine()); }
						catch (DateTimeParseException e) { System.out.println("Formato de fecha inválido."); break; }
						System.out.println("  Total ventas del día:       $" + logica.getTotalVentasDia(fecha));
						System.out.println("  Ventas de juegos:           $" + logica.getTotalVentasJuegos(fecha, fecha));
						System.out.println("  Ventas de cafetería:        $" + logica.getTotalVentasCafe(fecha, fecha));
						System.out.println("  Total impuestos (IVA+cons): $" + logica.getTotalImpuestos(fecha, fecha));
						System.out.println("  Total propinas:             $" + logica.getTotalPropinas(fecha, fecha));
						System.out.println("  Total costos (subtotales):  $" + logica.getTotalCostos(fecha, fecha));

					} else if (subOpc11.equals("2")) {
						System.out.print("Fecha de inicio de semana (YYYY-MM-DD): ");
						LocalDate inicioSemana = null;
						try { inicioSemana = LocalDate.parse(scanner.nextLine()); }
						catch (DateTimeParseException e) { System.out.println("Formato de fecha inválido."); break; }
						LocalDate finSemana = inicioSemana.plusDays(6);
						System.out.println("  Total ventas semana:        $" + logica.getTotalVentasSemana(inicioSemana));
						System.out.println("  Ventas de juegos:           $" + logica.getTotalVentasJuegos(inicioSemana, finSemana));
						System.out.println("  Ventas de cafetería:        $" + logica.getTotalVentasCafe(inicioSemana, finSemana));
						System.out.println("  Total impuestos:            $" + logica.getTotalImpuestos(inicioSemana, finSemana));
						System.out.println("  Total propinas:             $" + logica.getTotalPropinas(inicioSemana, finSemana));
						System.out.println("  Total costos (subtotales):  $" + logica.getTotalCostos(inicioSemana, finSemana));

					} else if (subOpc11.equals("3")) {
						System.out.print("Mes (1-12): ");
						int mes = 0;
						try { mes = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						System.out.print("Año (ej: 2025): ");
						int anio = 0;
						try { anio = Integer.parseInt(scanner.nextLine()); }
						catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
						LocalDate inicioMes = LocalDate.of(anio, mes, 1);
						LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
						System.out.println("  Total ventas mes: $" + logica.getTotalVentasMes(mes, anio));
						System.out.println("  Ventas de juegos: $" + logica.getTotalVentasJuegos(inicioMes, finMes));
						System.out.println("  Ventas de cafetería: $" + logica.getTotalVentasCafe(inicioMes, finMes));
						System.out.println("  Total impuestos: $" + logica.getTotalImpuestos(inicioMes, finMes));
						System.out.println("  Total propinas: $" + logica.getTotalPropinas(inicioMes, finMes));
						System.out.println("  Total costos (subtotales): $" + logica.getTotalCostos(inicioMes, finMes));
					} else {
						System.out.println("Opción inválida.");
					}
					break;

				// SUGERENCIAS DE PLATILLOS
				case "12":
					System.out.println("\n--- Aprobar/Rechazar Sugerencias de Platillos ---");
					List<SugerenciaPlatillo> sugerencias = logica.getSugerenciasPendientes();
					if (sugerencias.isEmpty()) {
						System.out.println("No hay sugerencias pendientes.");
						break;
					}
					System.out.println("Sugerencias pendientes:");
					for (int i = 0; i < sugerencias.size(); i++) {
						SugerenciaPlatillo s = sugerencias.get(i);
						System.out.println((i + 1) + ". Empleado: " + s.getEmpleado().getLogin() +
								" | Item: " + s.getItemSugerido().getNombre() +
								" ($" + s.getItemSugerido().getPrecio() + ")" +
								" | Descripción: " + s.getDescripcion());
					}
					System.out.print("Número de sugerencia a gestionar (0 para cancelar): ");
					int numSug = 0;
					try { numSug = Integer.parseInt(scanner.nextLine()); }
					catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
					if (numSug == 0 || numSug > sugerencias.size()) { System.out.println("Cancelado."); break; }
					System.out.print("¿(A)probar o (R)echazar?: ");
					String decSug = scanner.nextLine();
					if (decSug.equalsIgnoreCase("A")) {
						logica.aprobarSugerencia(adminLogueado, sugerencias.get(numSug - 1));
						System.out.println("Sugerencia aprobada. Item añadido al menú.");
					} else if (decSug.equalsIgnoreCase("R")) {
						logica.rechazarSugerencia(adminLogueado, sugerencias.get(numSug - 1));
						System.out.println("Sugerencia rechazada.");
					} else {
						System.out.println("Opción inválida.");
					}
					break;

				// ─── 13. PREMIAR GANADOR ────────────────────────────────────────
				case "13":
					System.out.println("\n--- Premiar Ganador de Torneo ---");
					List<Torneo> torneos = cafe.getTorneos();
					if (torneos.isEmpty()) { System.out.println("No hay torneos registrados."); break; }
					System.out.println("Torneos disponibles:");
					for (int i = 0; i < torneos.size(); i++) {
						Torneo t = torneos.get(i);
						String tipTorneo = (t instanceof TorneoCompetitivo) ? "Competitivo" : "Amistoso";
						System.out.println("  " + (i + 1) + ". " + t.getJuego().getNombre() +
								" | Día: " + t.getDiaSemana() + " | Tipo: " + tipTorneo);
					}
					System.out.print("Número del torneo: ");
					int numTorneo = 0;
					try { numTorneo = Integer.parseInt(scanner.nextLine()); }
					catch (NumberFormatException e) { System.out.println("Número inválido."); break; }
					if (numTorneo < 1 || numTorneo > torneos.size()) { System.out.println("Opción fuera de rango."); break; }
					Torneo torneoElegido = torneos.get(numTorneo - 1);

					System.out.print("Login del ganador: ");
					String loginGanador = scanner.nextLine();
					Usuario ganador = cafe.buscarUsuarioPorLogin(loginGanador);
					if (ganador == null) { System.out.println("Usuario no encontrado."); break; }
					if (!torneoElegido.getInscripciones().containsKey(ganador)) {
						System.out.println("El usuario no está inscrito en ese torneo.");
						break;
					}
					logica.premiarGanador(ganador, torneoElegido);
					if (torneoElegido instanceof TorneoCompetitivo) {
						System.out.println("Premio en metálico asignado a '" + loginGanador + "'.");
					} else {
						System.out.println("Bono de descuento del 33% asignado a '" + loginGanador + "' para su próxima compra.");
					}
					break;

				// ─── 14. SALIR Y GUARDAR ────────────────────────────────────────
				case "14":
					salir = true;
					try {
						System.out.println("\nGuardando datos en los archivos...");
						persistencia.CentralPersistencia.guardarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
						System.out.println("Datos guardados correctamente.");
					} catch (Exception e) {
						System.out.println("Error guardando los datos: " + e.getMessage());
					}
					System.out.println("Chao!");
					break;

				default:
					System.out.println("Opción inválida. Digite entre 1 y 14.");
			}
		}
		scanner.close();
	}
}
