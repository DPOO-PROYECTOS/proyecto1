package main;
import logica.CafeLogica;
import modelo.Cafe;
import modelo.Empleado;
import modelo.Turno;
import modelo.Usuario;

import java.util.List;
import java.util.Scanner;
public class MainEmpleado {
	public static void main(String[] args) {
		Scanner scanner= new Scanner(System.in);
		Cafe cafe= new Cafe("Uniandes Board", 100);
		CafeLogica logica= new CafeLogica(cafe);
		
		try {
            System.out.println("Cargando base de datos del Café...");
            persistencia.CentralPersistencia.cargarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json","data/turnos.json");
        } catch (Exception e) {
            System.out.println("Iniciando sistema en blanco (No se encontraron archivos previos).");
        }
		
		System.out.println("MODULO DE EMPLEADOS");
		
		Usuario empleadoLogueado= null;
		while (empleadoLogueado==null) {
			System.out.print("Ingrese usuario: ");
			String user= scanner.nextLine();
			System.out.print("Ingrese contraseña: ");
			String pass= scanner.nextLine();
			
			try {
				Usuario userTemp= logica.login(user, pass);
				
				String rol= userTemp.getClass().getSimpleName();
				if(rol.equals("Mesero") || rol.equalsIgnoreCase("Empleado")) {
					empleadoLogueado= userTemp;
					System.out.println("Login exitoso");
					System.out.println("Rol detectado: "+rol);
				} else {
					System.out.println("Acceso denegado. ");
				}
				
			}catch(Exception e) {
				System.out.println("Error: "+ e.getMessage());
			}
			
		}
		
		boolean salir= false;
		while(!salir) {
			System.out.println("\n  MENÚ STAFF ");
            System.out.println("1. Ver mi horario / turnos asignados");
            System.out.println("2. Solicitar un cambio de turno a un compañero");
            System.out.println("3. Tomar pedido de una mesa (Solo Meseros)");
            System.out.println("4. Sugerir un platillo al menú");
            System.out.println("5. Salir y Guardar");
            System.out.print("Seleccione una opción: ");
            
            String opcion=scanner.nextLine();
            
            switch(opcion) {
            	case "1":
            		System.out.println("Mis turnos");
            		boolean tieneTurnos= false;
            		if (!(empleadoLogueado instanceof Empleado)) {
            			System.out.println("Error, solo los empleados tienen turnos asignados");
            			break;}
            		
            		Empleado empActivo = (Empleado) empleadoLogueado;
            		
            		List<Turno> misTurnos=logica.getTurnosDeEmpleado(empActivo);
            		
            		if (misTurnos == null || misTurnos.isEmpty()) {
            			System.out.println("No tienes turnos programados en este momento");
            		} else {
            			System.out.println("Tus turnos asignados son: ");
            			for (Turno t: misTurnos) {
            				System.out.println("-Dia: "+ t.getDia() + 
            						"| Horario: "+ t.getHoraInicio() +" a " +t.getHoraFin());
            			}
            		}
            		break;
            		
            	case "2":
            		System.out.println("SOLICITUD DE CAMBIO DE TURNO");
            		
            		if (!(empleadoLogueado instanceof Empleado)) {
            			System.out.println("Error, solo los empleados pueden solicitar un cambio de turno");
            			break;
            		}
            		Empleado empLogueado= (Empleado) empleadoLogueado;
            		
            		System.out.print("Ingrese el dia de su turno que desea cambiar: ");
            		String diaTurno= scanner.nextLine();
            		
            		Turno turnoACambiar= null;
            		for (Turno t: logica.getTurnosDeEmpleado(empLogueado)) {
            			if (t.getDia().equalsIgnoreCase(diaTurno)) {
            				turnoACambiar= t;
            				break;
            			}
            		}
            		
            		if (turnoACambiar == null) {
            			System.out.println("Error; no tienes turno asignado para este día");
            			break;
            		}
            		
            		System.out.print("Ingrese el LOGIN del compañero con el que quiere cambiar (o presione Enter para dejar el turno libre)");
            		String compa= scanner.nextLine();
            		
            		Empleado compaEmp= null;
            		if(!compa.isBlank()) {
            			Usuario usu = cafe.buscarUsuarioPorLogin(compa);
            			if (usu instanceof Empleado) {
            				compaEmp = (Empleado) usu;
            			} else {
            				System.out.println("Error: el usuario no existe");
            			}
            		}
            		
            		try {
                        logica.solicitarCambioTurno(empLogueado, turnoACambiar, compaEmp);
                        System.out.println("¡Solicitud enviada exitosamente para revisión del Admin!");
                    } catch (Exception e) {
                        System.out.println("Error al solicitar el cambio: " + e.getMessage());
                    }
                    break;
            		
            	case "3":
                    System.out.println("\n TOMAR PEDIDO DE MESA ---");
                    
                    if (!empleadoLogueado.getClass().getSimpleName().equals("Mesero")) {
                        System.out.println("ERROR: Solo el personal con rol de 'Mesero' puede tomar pedidos en mesa.");
                        break;
                    }

                    System.out.print("Ingrese el NÚMERO de la mesa: ");
                    String numStr = scanner.nextLine();
                    int numMesa = 0;
                    try {
                        numMesa = Integer.parseInt(numStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Debe ingresar un número entero.");
                        break;
                    }
                    
                
                    modelo.Mesa mesaAtendida = logica.buscarMesaPorNumero(numMesa);
                    
                    if (mesaAtendida == null) {
                        System.out.println("Error: La mesa número " + numMesa + " no existe.");
                        break;
                    }
                    
                   
                    if (mesaAtendida.estaDisponible()) {
                        System.out.println(" Error: La mesa " + numMesa + " está vacía. Asigne un cliente primero.");
                        break;
                    }

                    System.out.print("Ingrese el nombre del platillo o bebida del menú a pedir: ");
                    String nombreItem = scanner.nextLine();

                    
                    modelo.ItemMenu itemEncontrado = null;
                    for (modelo.ItemMenu item : cafe.getMenu()) {
                        if (item.getNombre().equalsIgnoreCase(nombreItem)) { 
                            itemEncontrado = item;
                            break;
                        }
                    }

                    if (itemEncontrado == null) {
                        System.out.println("Error: '" + nombreItem + "' no se encuentra en el menú del café.");
                        break;
                    }

                    
                    java.util.List<modelo.ItemMenu> itemsPedido = new java.util.ArrayList<>();
                    itemsPedido.add(itemEncontrado);

                    
                    try {
                        logica.realizarPedidoCafe(empleadoLogueado, mesaAtendida, itemsPedido, -1.0, 0.0, false);
                        System.out.println("Pedido de '" + nombreItem + "' registrado en la mesa " + mesaAtendida.getNumero() + ".");
                    } catch (Exception e) {
                        System.out.println("Error registrando el pedido: " + e.getMessage());
                    }
                    break;
            	// ─── 4. SUGERIR PLATILLO ─────────────────────────────────────
            	case "4":
            		System.out.println("\n--- Sugerir un Platillo al Menú ---");
            		if (!(empleadoLogueado instanceof Empleado)) {
            			System.out.println("Solo los empleados pueden sugerir platillos.");
            			break;
            		}
            		Empleado empSugeridor = (Empleado) empleadoLogueado;
            		System.out.println("  1. Bebida");
            		System.out.println("  2. Pastelería");
            		System.out.print("Tipo de item a sugerir: ");
            		String tipoSug = scanner.nextLine();
            		System.out.print("Nombre del item: ");
            		String nombreSug = scanner.nextLine();
            		System.out.print("Precio sugerido: ");
            		double precioSug = 0;
            		try { precioSug = Double.parseDouble(scanner.nextLine()); }
            		catch (NumberFormatException e) { System.out.println("Precio inválido."); break; }
            		modelo.ItemMenu itemSugerido;
            		if (tipoSug.equals("1")) {
            			System.out.print("¿Es alcohólica? (S/N): ");
            			boolean esAlc = scanner.nextLine().equalsIgnoreCase("S");
            			System.out.print("¿Es caliente? (S/N): ");
            			boolean esCal = scanner.nextLine().equalsIgnoreCase("S");
            			itemSugerido = new modelo.Bebida(nombreSug, precioSug, esAlc, esCal);
            		} else if (tipoSug.equals("2")) {
            			modelo.Pasteleria past = new modelo.Pasteleria(nombreSug, precioSug);
            			System.out.print("¿Tiene alérgenos? (S/N): ");
            			if (scanner.nextLine().equalsIgnoreCase("S")) {
            				System.out.println("Ingrese alérgenos uno por uno (Enter vacío para terminar):");
            				while (true) {
            					System.out.print("  Alérgeno: ");
            					String alg = scanner.nextLine().trim();
            					if (alg.isEmpty()) break;
            					past.agregarAlergeno(alg);
            				}
            			}
            			itemSugerido = past;
            		} else {
            			System.out.println("Tipo inválido.");
            			break;
            		}
            		System.out.print("Descripción/justificación de la sugerencia: ");
            		String descSug = scanner.nextLine();
            		try {
            			logica.sugerirPlatillo(empSugeridor, descSug, itemSugerido);
            			System.out.println("Sugerencia enviada al administrador para su revisión.");
            		} catch (Exception e) {
            			System.out.println("Error al enviar sugerencia: " + e.getMessage());
            		}
            		break;

            	// ─── 5. SALIR Y GUARDAR ───────────────────────────────────────
            	case "5":
            		salir = true;
            		try {
            			System.out.println("Cerrando caja y guardando base de datos...");
            			persistencia.CentralPersistencia.guardarTodo(cafe, "data/usuarios.json", "data/inventarioPrestamos.json", "data/menu.json", "data/mesas.json", "data/inventarioVentas.json", "data/torneos.json", "data/turnos.json");
            		} catch (Exception e) {
            			System.out.println("Error guardando los datos: " + e.getMessage());
            		}
            		break;

            	default:
            		System.out.println("Opción invalida, digite un numero del 1 al 5");
            }
            
            
		}
		scanner.close();
		
		
	}
}
