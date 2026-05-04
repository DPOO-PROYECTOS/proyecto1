package main;

import logica.CafeLogica;
import modelo.Cafe;
import modelo.Cliente;
import modelo.ItemMenu;
import modelo.JuegoDeMesa;
import modelo.Mesa;
import modelo.PrestamoCliente;
import modelo.Torneo;
import modelo.VentaCafeteria;
import modelo.VentaJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class MainCliente {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Cafe cafe= new Cafe("Uniandes board", 100);
		CafeLogica logica= new CafeLogica(cafe);

		try {
            System.out.println("Cargando base de datos del Café...");
            persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
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
				System.out.print("Ingrese Usuario: ");
				String user= scanner.nextLine();
				System.out.print("Ingrese contraseña: ");
				String pass= scanner.nextLine();
				try {
					clienteLogueado= (Cliente) logica.login(user, pass);
					System.out.println("Hola de nuevo "+ user);
				} catch(Exception e){
					System.out.println("Error "+ e.getMessage());
				}
			}else if (opcLogin.equals("2")) {
				System.out.print("Cree un nuevo usuario: ");
				String newUser = scanner.nextLine();
				System.out.print("Cree una contraseña: ");
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
			// Mostrar estado de mesa actual
			Mesa mesaActual = null;
			for (Mesa m : cafe.getMesas()) {
				if (clienteLogueado.equals(m.getClienteAsignado())) {
					mesaActual = m;
					break;
				}
			}
			if (mesaActual != null) {
				System.out.println("\n[Mesa asignada: #" + mesaActual.getNumero() + " | Personas: " + mesaActual.getNumPersonas() + "]");
			} else {
				System.out.println("\n[Sin mesa asignada]");
			}

			System.out.println("\n=== Menu Cliente ===");
			System.out.println("1. Ver catálogo de juegos (Préstamo y Venta)");
            System.out.println("2. Inscribirse a un Torneo");
            System.out.println("3. Comprar un juego (Inventario Venta)");
            System.out.println("4. Reservar / Gestionar Mesa");
            System.out.println("5. Solicitar juego en préstamo");
            System.out.println("6. Ordenar del Menú");
            System.out.println("7. Salir y Guardar");
            System.out.print("Seleccione una opción: ");

			String opcion= scanner.nextLine();

			switch (opcion) {
				case "1":
					System.out.println("\n--- Catálogo de Préstamo ---");
					List<JuegoDeMesa> dispPrestamo = cafe.getInventarioPrestamo().getDisponibles();
					if (dispPrestamo.isEmpty()) {
						System.out.println("  (No hay juegos disponibles para préstamo)");
					} else {
						for (JuegoDeMesa j: cafe.getInventarioPrestamo().getJuegos()) {
							String estado = j.isDisponible() ? "Disponible" : "Prestado";
							System.out.println("  - " + j.getNombre() + " [" + estado + "] | Veces prestado: " + j.getVecesPrestado());
						}
					}
					System.out.println("\n--- Catálogo de Venta ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("  (No hay juegos en venta)");
					} else {
						for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
							System.out.println("  - " + j.getNombre() + " | Precio: $" + j.getPrecioVenta());
						}
					}
					break;

				case "2":
					System.out.println("\n--- Torneos Disponibles ---");
					if (cafe.getTorneos().isEmpty()) {
						System.out.println("No hay torneos activos.");
						break;
					}
					for (Torneo t: cafe.getTorneos()) {
						System.out.println("  - Juego: " + t.getJuego().getNombre() +
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
						System.out.println("No se encontró torneo para ese juego.");
						break;
					}

					System.out.print("Cuantos cupos desea reservar: ");
					String cupos= scanner.nextLine();
					int cuposReserva=0;
					try {
					    cuposReserva = Integer.parseInt(cupos);
					} catch (NumberFormatException e) {
					    System.out.println("Debe ingresar un número entero.");
					    break;
					}
					try {
						logica.inscribirEnTorneo(clienteLogueado, torneoElegido, cuposReserva);
						System.out.println("Registro exitoso.");
					}catch(Exception e) {
						System.out.println("Error en la inscripción: "+ e.getMessage());
					}
					break;

				case "3":
					System.out.println("\n--- Inventario de Venta ---");
					if (cafe.getInventarioVenta().getJuegos().isEmpty()) {
						System.out.println("No hay juegos disponibles para comprar.");
						break;
					}
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						System.out.println("  - " + j.getNombre() + " | Precio: $" + j.getPrecioVenta());
					}
					System.out.print("Ingrese el nombre del juego que desea comprar: ");
					String nombreJuego= scanner.nextLine();
					JuegoDeMesa juegoAComprar=null;
					for (JuegoDeMesa j: cafe.getInventarioVenta().getJuegos()) {
						if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
							juegoAComprar=j;
							break;
						}
					}
					if (juegoAComprar==null) {
						System.out.println("El juego no está en el inventario de ventas.");
						break;
					}
					System.out.print("Tienes algún descuento de empleado? (Presiona Enter si no): ");
					String codigoDesc= scanner.nextLine();
					boolean usarBonoTorneo = clienteLogueado.getTieneBonoTorneoAmistoso();
					if (usarBonoTorneo) {
						System.out.println("Tienes un bono de torneo amistoso. Se aplicará automáticamente.");
					}
					System.out.print("Cuántos puntos de fidelidad deseas usar? (Tienes " + clienteLogueado.getPuntosFidelidad() + " puntos, Enter=0): ");
					String puntosUs= scanner.nextLine();
					int puntosUsar=0;
					if (!puntosUs.isEmpty()) {
					    try {
					        puntosUsar = Integer.parseInt(puntosUs);
					    } catch (NumberFormatException e) {
					        System.out.println("Número de puntos inválido. Se usarán 0 puntos.");
					    }
					}
					try {
						ArrayList<JuegoDeMesa> juegosComprar= new ArrayList<JuegoDeMesa>();
						juegosComprar.add(juegoAComprar);
						VentaJuego venta= logica.venderJuegos(clienteLogueado, juegosComprar, codigoDesc, puntosUsar, usarBonoTorneo);
						System.out.println("Compra exitosa. Total pagado (con IVA y descuentos): $" + venta.calcularTotal());
					    System.out.println("Tus puntos de fidelidad actuales: " + clienteLogueado.getPuntosFidelidad());
					}catch(Exception e) {
						System.out.println("Error al procesar compra: " + e.getMessage());
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "4":
					System.out.println("\n=== Gestión de Mesa ===");
					if (mesaActual != null) {
						System.out.println("Ya tienes la mesa #" + mesaActual.getNumero() + " asignada.");
						System.out.println("  1. Liberar mesa (salir del local)");
						System.out.println("  2. Volver al menú principal");
						System.out.print("Opción: ");
						String subOpc4 = scanner.nextLine();
						if (subOpc4.equals("1")) {
							logica.liberarMesa(mesaActual);
							System.out.println("Mesa liberada. ¡Hasta pronto!");
						}
					} else {
						// Mostrar mesas disponibles
						System.out.println("Mesas disponibles:");
						boolean hayDisponibles = false;
						for (Mesa m : cafe.getMesas()) {
							if (m.estaDisponible()) {
								System.out.println("  - Mesa #" + m.getNumero() + " | Capacidad: " + m.getCapacidad() + " personas");
								hayDisponibles = true;
							}
						}
						if (!hayDisponibles) {
							System.out.println("  (No hay mesas disponibles en este momento)");
							break;
						}
						System.out.print("¿Cuántas personas vienen? ");
						String strPersonas = scanner.nextLine();
						int numPersonas;
						try {
							numPersonas = Integer.parseInt(strPersonas);
						} catch (NumberFormatException e) {
							System.out.println("Número inválido.");
							break;
						}
						System.out.print("¿Hay niños menores de 5 años? (S/N): ");
						boolean tieneNinos = scanner.nextLine().equalsIgnoreCase("S");
						System.out.print("¿Hay jóvenes? (S/N): ");
						boolean tieneJovenes = scanner.nextLine().equalsIgnoreCase("S");
						try {
							Mesa mesaAsignada = logica.asignarMesa(clienteLogueado, numPersonas, tieneNinos, tieneJovenes);
							System.out.println("Mesa #" + mesaAsignada.getNumero() + " asignada correctamente. ¡Disfruten!");
						} catch (Exception e) {
							System.out.println("No se pudo asignar mesa: " + e.getMessage());
						}
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "5":
					System.out.println("\n=== Solicitar Juego en Préstamo ===");
					if (mesaActual == null) {
						System.out.println("Debes tener una mesa asignada para solicitar un juego.");
						break;
					}
					// Mostrar disponibles para prestamo
					List<JuegoDeMesa> disponibles = cafe.getInventarioPrestamo().getDisponibles();
					if (disponibles.isEmpty()) {
						System.out.println("No hay juegos disponibles para préstamo en este momento.");
						break;
					}
					System.out.println("Juegos disponibles para préstamo:");
					for (JuegoDeMesa j : disponibles) {
						System.out.println("  - " + j.getNombre()
								+ " | Jugadores: " + j.getMinJugadores() + "-" + j.getMaxJugadores()
								+ " | Veces prestado: " + j.getVecesPrestado());
					}
					// Opcion de devolver
					PrestamoCliente prestamoActivo = logica.getPrestamoActivoCliente(clienteLogueado);
					if (prestamoActivo != null && !prestamoActivo.getJuegos().isEmpty()) {
						System.out.println("\nJuegos que tienes en préstamo actualmente:");
						for (JuegoDeMesa j : prestamoActivo.getJuegos()) {
							System.out.println("  - " + j.getNombre());
						}
						System.out.println("\n  1. Solicitar otro juego (máx. 2)");
						System.out.println("  2. Devolver un juego");
						System.out.println("  3. Volver");
						System.out.print("Opción: ");
						String subOpc5 = scanner.nextLine();
						if (subOpc5.equals("2")) {
							System.out.print("Nombre del juego a devolver: ");
							String nombreDev = scanner.nextLine();
							JuegoDeMesa juegoDev = null;
							for (JuegoDeMesa j : prestamoActivo.getJuegos()) {
								if (j.getNombre().equalsIgnoreCase(nombreDev)) {
									juegoDev = j;
									break;
								}
							}
							if (juegoDev == null) {
								System.out.println("No tienes ese juego en préstamo.");
								break;
							}
							logica.devolverJuego(prestamoActivo, juegoDev);
							System.out.println("Juego '" + juegoDev.getNombre() + "' devuelto correctamente.");
							break;
						} else if (!subOpc5.equals("1")) {
							break;
						}
					}
					System.out.print("Ingrese el nombre del juego que desea solicitar: ");
					String nombrePrestamo = scanner.nextLine();
					try {
						PrestamoCliente prestamo = logica.solicitarPrestamoCliente(clienteLogueado, nombrePrestamo);
						JuegoDeMesa jPrestado = null;
						for (JuegoDeMesa j : prestamo.getJuegos()) {
							if (j.getNombre().equalsIgnoreCase(nombrePrestamo)) {
								jPrestado = j;
								break;
							}
						}
						System.out.println("Juego '" + nombrePrestamo + "' prestado exitosamente.");
						if (jPrestado != null) {
							System.out.println("  Veces prestado (histórico): " + jPrestado.getVecesPrestado());
						}
					} catch (Exception e) {
						System.out.println("Error al solicitar préstamo: " + e.getMessage());
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "6":
					System.out.println("\n=== Ordenar del Menú ===");
					if (mesaActual == null) {
						System.out.println("Debes tener una mesa asignada para ordenar.");
						break;
					}
					if (cafe.getMenu().isEmpty()) {
						System.out.println("El menú está vacío.");
						break;
					}
					System.out.println("Menú disponible:");
					List<ItemMenu> menuItems = cafe.getMenu();
					for (int i = 0; i < menuItems.size(); i++) {
						ItemMenu item = menuItems.get(i);
						System.out.println("  " + (i + 1) + ". " + item.getNombre() + " | $" + item.getPrecio()
								+ " [" + item.getClass().getSimpleName() + "]");
					}
					List<ItemMenu> pedido = new ArrayList<>();
					boolean siguiendoPedido = true;
					while (siguiendoPedido) {
						System.out.print("Número del item a agregar (0 para terminar): ");
						String strItem = scanner.nextLine();
						int numItem;
						try {
							numItem = Integer.parseInt(strItem);
						} catch (NumberFormatException e) {
							System.out.println("Ingrese un número válido.");
							continue;
						}
						if (numItem == 0) {
							siguiendoPedido = false;
						} else if (numItem < 1 || numItem > menuItems.size()) {
							System.out.println("Número fuera de rango.");
						} else {
							pedido.add(menuItems.get(numItem - 1));
							System.out.println("  Agregado: " + menuItems.get(numItem - 1).getNombre());
						}
					}
					if (pedido.isEmpty()) {
						System.out.println("No agregaste ningún item.");
						break;
					}
					System.out.print("¿Deseas dejar propina? Ingresa el monto (Enter = propina sugerida): ");
					String strPropina = scanner.nextLine();
					double propina = -1;
					if (!strPropina.isEmpty()) {
						try {
							propina = Double.parseDouble(strPropina);
						} catch (NumberFormatException e) {
							System.out.println("Monto inválido, se usará propina sugerida.");
						}
					}
					System.out.print("¿Puntos de fidelidad a usar? (Tienes " + clienteLogueado.getPuntosFidelidad() + ", Enter=0): ");
					String strPuntosCafe = scanner.nextLine();
					double puntosCafe = 0;
					if (!strPuntosCafe.isEmpty()) {
						try {
							puntosCafe = Double.parseDouble(strPuntosCafe);
						} catch (NumberFormatException e) {
							System.out.println("Valor inválido, se usarán 0 puntos.");
						}
					}
					System.out.print("¿Tienes un bono de torneo amistoso para aplicar? (S/N): ");
					boolean usarBonoCafe = scanner.nextLine().equalsIgnoreCase("S");
					try {
						VentaCafeteria ventaCafe = logica.realizarPedidoCafe(clienteLogueado, mesaActual, pedido, propina, puntosCafe, usarBonoCafe);
						System.out.println("Pedido realizado con éxito.");
						System.out.println("  Subtotal:  $" + ventaCafe.calcularSubtotal());
						System.out.println("  Total:     $" + ventaCafe.calcularTotal());
						System.out.println("  Propina:   $" + ventaCafe.getPropina());
						System.out.println("Tus puntos de fidelidad actuales: " + clienteLogueado.getPuntosFidelidad());
					} catch (Exception e) {
						System.out.println("Error al procesar pedido: " + e.getMessage());
					}
					break;

				// ─────────────────────────────────────────────────────────────
				case "7":
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
					System.out.println("Opcion invalida. Digite entre 1 y 7.");
			}
		}

		scanner.close();		

		
	}
}
