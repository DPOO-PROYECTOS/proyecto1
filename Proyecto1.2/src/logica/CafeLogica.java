package logica;

import modelo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CafeLogica {

    // ===== ATRIBUTOS =====
    private Cafe cafe;

    // ===== CONSTRUCTOR =====
    public CafeLogica(Cafe cafe) {
        this.cafe = cafe;
    }

    public Cafe getCafe() { return cafe; }

    // ==========================================================
    // AUTENTICACIÓN Y USUARIOS
    // ==========================================================

    public Usuario login(String login, String password) {
        Usuario u = cafe.buscarUsuarioPorLogin(login);
        if (u == null)
            throw new IllegalArgumentException("No existe un usuario con login: " + login);
        if (!u.verificarPassword(password))
            throw new IllegalArgumentException("Contraseña incorrecta.");
        return u;
    }

    public Cliente registrarCliente(String login, String password) {
        if (cafe.buscarUsuarioPorLogin(login) != null)
            throw new IllegalArgumentException("El login '" + login + "' ya está en uso.");
        Cliente cliente = new Cliente(login, password);
        cafe.agregarUsuario(cliente);
        return cliente;
    }

    public Empleado registrarEmpleado(String login, String password, String tipo) {
        if (cafe.buscarUsuarioPorLogin(login) != null)
            throw new IllegalArgumentException("El login '" + login + "' ya está en uso.");
        Empleado empleado;
        if ("mesero".equalsIgnoreCase(tipo)) {
            empleado = new Mesero(login, password);
        } else if ("cocinero".equalsIgnoreCase(tipo)) {
            empleado = new Empleado(login, password, "cocinero");
        } else {
            throw new IllegalArgumentException("Tipo de empleado inválido: '" + tipo + "'. Use 'mesero' o 'cocinero'.");
        }
        cafe.agregarUsuario(empleado);
        return empleado;
    }

    // ==========================================================
    // GESTIÓN DE MESAS
    // ==========================================================

    public Mesa asignarMesa(Cliente cliente, int numPersonas, boolean tieneNinos, boolean tieneJovenes) {
        if (numPersonas <= 0)
            throw new IllegalArgumentException("El número de personas debe ser mayor a 0.");
        if (!cafe.hayCapacidad(numPersonas))
            throw new IllegalStateException("El café ha alcanzado su capacidad máxima de " + cafe.getCapacidadMaxima() + " personas.");
        Mesa mesa = cafe.buscarMesaDisponible(numPersonas);
        if (mesa == null)
            throw new IllegalStateException("No hay mesas disponibles con capacidad para " + numPersonas + " personas.");
        mesa.setClienteAsignado(cliente);
        mesa.setNumPersonas(numPersonas);
        mesa.setTieneNinos(tieneNinos);
        mesa.setTieneJovenes(tieneJovenes);
        return mesa;
    }

    public void liberarMesa(Mesa mesa) {
        Cliente cliente = mesa.getClienteAsignado();
        if (cliente != null) {
            PrestamoCliente prestamo = getPrestamoActivoCliente(cliente);
            if (prestamo != null) {
                finalizarPrestamo(prestamo);
            }
        }
        mesa.liberar();
    }

    // ==========================================================
    // GESTIÓN DE PRÉSTAMOS
    // ==========================================================

    public PrestamoCliente solicitarPrestamoCliente(Cliente cliente, String nombreJuego) {
        Mesa mesa = getMesaDeCliente(cliente);
        if (mesa == null)
            throw new IllegalStateException("El cliente no tiene una mesa asignada. Debe ocupar una mesa primero.");

        PrestamoCliente prestamoActivo = getPrestamoActivoCliente(cliente);
        if (prestamoActivo != null && prestamoActivo.getJuegos().size() >= 2)
            throw new IllegalStateException("El cliente ya tiene 2 juegos en préstamo (máximo permitido).");

        JuegoDeMesa juego = cafe.getInventarioPrestamo().buscarDisponiblePorNombre(nombreJuego);
        if (juego == null) {
            // El juego puede existir pero no estar disponible
            JuegoDeMesa existente = cafe.getInventarioPrestamo().buscarPorNombre(nombreJuego);
            if (existente != null)
                throw new IllegalStateException("El juego '" + nombreJuego + "' existe pero actualmente está prestado a otros clientes.");
            throw new IllegalStateException("El juego '" + nombreJuego + "' no se encuentra en el inventario de préstamo.");
        }

        if (!juego.esAptoParaMesa(mesa.getNumPersonas(), mesa.isTieneNinos(), mesa.isTieneJovenes()))
            throw new IllegalStateException("El juego '" + nombreJuego + "' no es apto para esta mesa (verificar número de jugadores o restricción de edad).");

        if (juego instanceof JuegoDeAccion && mesa.tieneBebidaCaliente())
            throw new IllegalStateException("No se puede prestar un juego de Acción a una mesa que tiene bebidas calientes.");

        if (juego instanceof JuegoDificil) {
            boolean hayMesero = hayMeseroDisponibleParaJuegoDificil((JuegoDificil) juego);
            if (!hayMesero)
                System.out.println("Advertencia: No hay mesero capacitado para enseñar '" + nombreJuego + "'. El juego se presta bajo responsabilidad del cliente.");
            else
                System.out.println("Información: Hay un mesero disponible para introducir el juego '" + nombreJuego + "'.");
        }

        if (prestamoActivo == null) {
            prestamoActivo = new PrestamoCliente(LocalDateTime.now(), cliente, mesa);
            cafe.registrarPrestamo(prestamoActivo);
        }

        prestamoActivo.agregarJuego(juego);
        return prestamoActivo;
    }

    public PrestamoEmpleado solicitarPrestamoEmpleado(Empleado empleado, String nombreJuego) {
        if (empleado.isEnTurno() && hayClientesEnElCafe())
            throw new IllegalStateException("El empleado está en turno y hay clientes que atender. No puede pedir prestado ahora.");

        JuegoDeMesa juego = cafe.getInventarioPrestamo().buscarDisponiblePorNombre(nombreJuego);
        if (juego == null) {
            JuegoDeMesa existente = cafe.getInventarioPrestamo().buscarPorNombre(nombreJuego);
            if (existente != null)
                throw new IllegalStateException("El juego '" + nombreJuego + "' está actualmente prestado.");
            throw new IllegalStateException("El juego '" + nombreJuego + "' no se encuentra en el inventario de préstamo.");
        }

        if (juego instanceof JuegoDificil) {
            boolean hayMesero = hayMeseroDisponibleParaJuegoDificil((JuegoDificil) juego);
            if (!hayMesero)
                System.out.println("Advertencia: No hay mesero capacitado para enseñar '" + nombreJuego + "'.");
        }

        PrestamoEmpleado prestamo = new PrestamoEmpleado(LocalDateTime.now(), empleado);
        prestamo.agregarJuego(juego);
        cafe.registrarPrestamo(prestamo);
        return prestamo;
    }

    public void devolverJuego(Prestamo prestamo, JuegoDeMesa juego) {
        if (!prestamo.getJuegos().contains(juego))
            throw new IllegalArgumentException("El juego '" + juego.getNombre() + "' no pertenece a este préstamo.");
        prestamo.devolverJuego(juego);
        if (prestamo.getJuegos().isEmpty()) {
            prestamo.finalizar();
        }
    }

    public void finalizarPrestamo(Prestamo prestamo) {
        prestamo.finalizar();
    }

    public PrestamoCliente getPrestamoActivoCliente(Cliente cliente) {
        if (cliente == null) return null;
        return cafe.getHistorialPrestamos().stream()
                .filter(p -> p instanceof PrestamoCliente)
                .map(p -> (PrestamoCliente) p)
                .filter(p -> p.getCliente().equals(cliente) && p.estaActivo())
                .findFirst()
                .orElse(null);
    }

    public List<Prestamo> getPrestamosActivos() {
        return cafe.getHistorialPrestamos().stream()
                .filter(Prestamo::estaActivo)
                .collect(Collectors.toList());
    }

    private boolean hayMeseroDisponibleParaJuegoDificil(JuegoDificil juego) {
        return cafe.getUsuarios().stream()
                .filter(u -> u instanceof Mesero)
                .map(u -> (Mesero) u)
                .anyMatch(m -> m.conoceJuego(juego));
    }

    private boolean hayClientesEnElCafe() {
        return cafe.getMesas().stream().anyMatch(m -> !m.estaDisponible());
    }

    private Mesa getMesaDeCliente(Cliente cliente) {
        return cafe.getMesas().stream()
                .filter(m -> cliente.equals(m.getClienteAsignado()))
                .findFirst()
                .orElse(null);
    }

    // ==========================================================
    // VENTAS DE JUEGOS
    // ==========================================================

    public VentaJuego venderJuegos(Usuario usuario, List<JuegoDeMesa> juegos,
                                    String codigoDescuento, double puntosAUsar) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede realizar compras de juegos.");
        if (juegos == null || juegos.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos un juego.");

        VentaJuego venta = new VentaJuego(LocalDateTime.now(), usuario);

        if (codigoDescuento != null && !codigoDescuento.isBlank()) {
            Empleado empleadoDesc = cafe.buscarEmpleadoPorCodigo(codigoDescuento);
            if (empleadoDesc == null)
                throw new IllegalArgumentException("Código de descuento inválido: '" + codigoDescuento + "'.");
            venta.setEmpleadoDescuento(empleadoDesc);
        }

        for (JuegoDeMesa juego : juegos) {
            if (!cafe.getInventarioVenta().estaEnInventario(juego))
                throw new IllegalStateException("El juego '" + juego.getNombre() + "' no está disponible en el inventario de ventas.");
            venta.agregarLinea(new LineaVenta(1, juego.getPrecioVenta(), juego));
        }

        if (puntosAUsar > 0) {
            if (!(usuario instanceof Cliente))
                throw new IllegalStateException("Solo los clientes pueden usar puntos de fidelidad.");
            if (!((Cliente) usuario).usarPuntos(puntosAUsar))
                throw new IllegalStateException("El cliente no tiene suficientes puntos de fidelidad. Disponibles: " + ((Cliente) usuario).getPuntosFidelidad());
        }

        for (JuegoDeMesa juego : juegos) {
            cafe.getInventarioVenta().quitarJuego(juego);
        }

        otorgarPuntosFidelidad(usuario, venta.calcularTotal());
        cafe.agregarVenta(venta);
        return venta;
    }

    // ==========================================================
    // VENTAS DE CAFETERÍA
    // ==========================================================

    public VentaCafeteria realizarPedidoCafe(Usuario usuario, Mesa mesa,
                                              List<ItemMenu> items, double propina,
                                              double puntosAUsar) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede realizar pedidos en el café.");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos un ítem del menú.");

        VentaCafeteria venta = new VentaCafeteria(LocalDateTime.now(), usuario);

        if (usuario instanceof Empleado) {
            venta.setEmpleadoDescuento((Empleado) usuario);
        }

        for (ItemMenu item : items) {
            validarItemParaMesa(item, mesa);
            venta.agregarLinea(new LineaVenta(1, item.getPrecio(), item));
        }

        // Propina: negativo = usar la sugerida (10%), 0 = sin propina, >0 = monto personalizado
        if (propina < 0) {
            venta.setPropina(venta.calcularPropinaSugerida());
        } else {
            venta.setPropina(propina);
        }

        if (puntosAUsar > 0) {
            if (!(usuario instanceof Cliente))
                throw new IllegalStateException("Solo los clientes pueden usar puntos de fidelidad.");
            if (!((Cliente) usuario).usarPuntos(puntosAUsar))
                throw new IllegalStateException("El cliente no tiene suficientes puntos de fidelidad. Disponibles: " + ((Cliente) usuario).getPuntosFidelidad());
        }

        // Registrar bebidas en la mesa para validaciones posteriores de préstamo
        for (ItemMenu item : items) {
            if (item instanceof Bebida) {
                mesa.agregarBebida((Bebida) item);
            }
        }

        otorgarPuntosFidelidad(usuario, venta.calcularSubtotal());
        cafe.agregarVenta(venta);
        return venta;
    }

    private void validarItemParaMesa(ItemMenu item, Mesa mesa) {
        if (item instanceof Bebida) {
            Bebida bebida = (Bebida) item;
            if (bebida.isEsAlcoholica() && mesa.tieneMenores())
                throw new IllegalStateException("No se puede servir bebida alcohólica '" + bebida.getNombre() + "' a una mesa con menores de edad.");
            if (bebida.isEsCaliente()) {
                PrestamoCliente prestamo = getPrestamoActivoCliente(mesa.getClienteAsignado());
                if (prestamo != null && prestamo.getJuegos().stream().anyMatch(j -> j instanceof JuegoDeAccion))
                    throw new IllegalStateException("No se puede servir bebida caliente a una mesa que tiene un juego de Acción activo.");
            }
        }
        if (item instanceof Pasteleria) {
            Pasteleria p = (Pasteleria) item;
            if (!p.getAlergenos().isEmpty()) {
                System.out.println("Advertencia de alérgenos en '" + p.getNombre() + "': " + String.join(", ", p.getAlergenos()));
            }
        }
    }

    // ==========================================================
    // GESTIÓN DE INVENTARIO (ADMIN)
    // ==========================================================

    public void agregarJuegoInventarioPrestamo(JuegoDeMesa juego) {
        juego.setDisponible(true);
        cafe.getInventarioPrestamo().agregarJuego(juego);
    }

    public void agregarJuegoInventarioVenta(JuegoDeMesa juego, double precio) {
        juego.setPrecioVenta(precio);
        cafe.getInventarioVenta().agregarJuego(juego);
    }

    public void moverDeVentaAPrestamo(JuegoDeMesa juego) {
        if (!cafe.getInventarioVenta().estaEnInventario(juego))
            throw new IllegalStateException("El juego '" + juego.getNombre() + "' no se encuentra en el inventario de ventas.");
        cafe.getInventarioVenta().quitarJuego(juego);
        juego.setDisponible(true);
        cafe.getInventarioPrestamo().agregarJuego(juego);
    }

    public void repararJuego(JuegoDeMesa juegoRoto) {
        if (!cafe.getInventarioPrestamo().getJuegos().contains(juegoRoto))
            throw new IllegalStateException("El juego a reparar no está en el inventario de préstamo.");
        JuegoDeMesa reemplazo = cafe.getInventarioVenta().buscarPorNombre(juegoRoto.getNombre());
        if (reemplazo == null)
            throw new IllegalStateException("No hay copia de reemplazo en inventario de ventas para '" + juegoRoto.getNombre() + "'.");
        cafe.getInventarioPrestamo().quitarJuego(juegoRoto);
        cafe.getInventarioVenta().quitarJuego(reemplazo);
        reemplazo.setDisponible(true);
        reemplazo.setVecesPrestado(juegoRoto.getVecesPrestado());
        cafe.getInventarioPrestamo().agregarJuego(reemplazo);
    }

    public void marcarComoRobado(JuegoDeMesa juego) {
        juego.setEstado("desaparecido");
        juego.setDisponible(false);
        cafe.getInventarioPrestamo().quitarJuego(juego);
    }

    public void actualizarEstadoJuego(JuegoDeMesa juego, String nuevoEstado) {
        juego.setEstado(nuevoEstado);
    }

    // ==========================================================
    // GESTIÓN DE TURNOS
    // ==========================================================

    public Turno crearTurno(Empleado empleado, String dia, String horaInicio, String horaFin) {
        Turno turno = new Turno(dia, horaInicio, horaFin, empleado);
        cafe.getPlanSemanal().agregarTurno(turno);
        return turno;
    }

    public void eliminarTurno(Turno turno) {
        String dia = turno.getDia();
        PlanSemanal plan = cafe.getPlanSemanal();
        plan.quitarTurno(turno);
        if (!plan.cumpleMinimosDia(dia)) {
            plan.agregarTurno(turno);
            throw new IllegalStateException("No se puede eliminar el turno: el día " + dia + " quedaría por debajo del mínimo requerido (1 cocinero, 2 meseros).");
        }
        turno.getEmpleado().setEnTurno(false);
    }

    public void modificarTurno(Turno turno, String nuevoDia, String nuevaHoraInicio, String nuevaHoraFin) {
        String diaOriginal = turno.getDia();
        String horaInicioOriginal = turno.getHoraInicio();
        String horaFinOriginal = turno.getHoraFin();
        PlanSemanal plan = cafe.getPlanSemanal();

        turno.setDia(nuevoDia);
        turno.setHoraInicio(nuevaHoraInicio);
        turno.setHoraFin(nuevaHoraFin);

        if (!plan.cumpleMinimosDia(diaOriginal)) {
            turno.setDia(diaOriginal);
            turno.setHoraInicio(horaInicioOriginal);
            turno.setHoraFin(horaFinOriginal);
            throw new IllegalStateException("La modificación dejaría el día " + diaOriginal + " sin el mínimo requerido de empleados.");
        }
    }

    public SolicitudCambioTurno solicitarCambioTurno(Empleado solicitante, Turno turno, Empleado intercambiarCon) {
        // Verificar que el turno pertenece al solicitante
        if (!turno.getEmpleado().equals(solicitante))
            throw new IllegalArgumentException("El turno no pertenece al empleado solicitante.");

        // Para cambio general (sin intercambio): verificar que aún se cumplen los mínimos
        if (intercambiarCon == null) {
            PlanSemanal plan = cafe.getPlanSemanal();
            plan.quitarTurno(turno);
            boolean cumple = plan.cumpleMinimosDia(turno.getDia());
            plan.agregarTurno(turno);
            if (!cumple)
                throw new IllegalStateException("No se puede solicitar el cambio: el día " + turno.getDia() + " quedaría sin el mínimo requerido de empleados.");
        }

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turno, solicitante, intercambiarCon);
        cafe.getSolicitudesCambioTurno().add(solicitud);
        return solicitud;
    }

    public void aprobarCambioTurno(Admin admin, SolicitudCambioTurno solicitud) {
        if (!"pendiente".equals(solicitud.getEstado()))
            throw new IllegalStateException("La solicitud ya fue procesada (estado: " + solicitud.getEstado() + ").");
        solicitud.aprobar(admin);

        Turno turno = solicitud.getTurno();
        Empleado solicitante = solicitud.getSolicitante();
        Empleado intercambiarCon = solicitud.getIntercambiarCon();

        if (intercambiarCon != null) {
            // Intercambio: buscar el turno del otro empleado en el mismo día y hacer el swap
            Turno turnoOtro = cafe.getPlanSemanal().getTurnosDia(turno.getDia()).stream()
                    .filter(t -> t.getEmpleado().equals(intercambiarCon))
                    .findFirst()
                    .orElse(null);
            turno.setEmpleado(intercambiarCon);
            if (turnoOtro != null) {
                turnoOtro.setEmpleado(solicitante);
            }
        } else {
            // Cambio general: quitar el turno al solicitante
            cafe.getPlanSemanal().quitarTurno(turno);
            solicitante.setEnTurno(false);
        }
    }

    public void rechazarCambioTurno(Admin admin, SolicitudCambioTurno solicitud) {
        if (!"pendiente".equals(solicitud.getEstado()))
            throw new IllegalStateException("La solicitud ya fue procesada (estado: " + solicitud.getEstado() + ").");
        solicitud.rechazar(admin);
    }

    public List<SolicitudCambioTurno> getSolicitudesPendientes() {
        return cafe.getSolicitudesCambioTurno().stream()
                .filter(s -> "pendiente".equals(s.getEstado()))
                .collect(Collectors.toList());
    }

    public List<Turno> getTurnosDeEmpleado(Empleado empleado) {
        return cafe.getPlanSemanal().getTurnosDeEmpleado(empleado);
    }

    // ==========================================================
    // GESTIÓN DE MENÚ Y SUGERENCIAS
    // ==========================================================

    public void agregarItemMenu(ItemMenu item) {
        cafe.agregarItemMenu(item);
    }

    public void removerItemMenu(ItemMenu item) {
        cafe.getMenu().remove(item);
    }

    public SugerenciaPlatillo sugerirPlatillo(Empleado empleado, String descripcion, ItemMenu item) {
        SugerenciaPlatillo sugerencia = new SugerenciaPlatillo(descripcion, item, empleado);
        cafe.getSugerencias().add(sugerencia);
        return sugerencia;
    }

    public void aprobarSugerencia(Admin admin, SugerenciaPlatillo sugerencia) {
        sugerencia.setAprobada(true);
        sugerencia.setAdmin(admin);
        cafe.agregarItemMenu(sugerencia.getItemSugerido());
    }

    public void rechazarSugerencia(Admin admin, SugerenciaPlatillo sugerencia) {
        sugerencia.setAprobada(false);
        sugerencia.setAdmin(admin);
    }

    public List<SugerenciaPlatillo> getSugerenciasPendientes() {
        return cafe.getSugerencias().stream()
                .filter(s -> !s.isAprobada() && s.getAdmin() == null)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // FAVORITOS
    // ==========================================================

    public void agregarFavorito(Usuario usuario, JuegoDeMesa juego) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede gestionar favoritos.");
        usuario.agregarFavorito(juego);
    }

    public void quitarFavorito(Usuario usuario, JuegoDeMesa juego) {
        usuario.quitarFavorito(juego);
    }

    // ==========================================================
    // REPORTES (ADMIN)
    // ==========================================================

    public double getTotalVentasDia(LocalDate fecha) {
        return filtrarVentas(fecha, fecha).stream()
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    public double getTotalVentasSemana(LocalDate inicioSemana) {
        return filtrarVentas(inicioSemana, inicioSemana.plusDays(6)).stream()
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    public double getTotalVentasMes(int mes, int anio) {
        return cafe.getVentas().stream()
                .filter(v -> v.getFecha().getMonthValue() == mes && v.getFecha().getYear() == anio)
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    public double getTotalVentasJuegos(LocalDate desde, LocalDate hasta) {
        return filtrarVentas(desde, hasta).stream()
                .filter(v -> v instanceof VentaJuego)
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    public double getTotalVentasCafe(LocalDate desde, LocalDate hasta) {
        return filtrarVentas(desde, hasta).stream()
                .filter(v -> v instanceof VentaCafeteria)
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    public double getTotalImpuestos(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = filtrarVentas(desde, hasta);

        double ivaJuegos = ventas.stream()
                .filter(v -> v instanceof VentaJuego)
                .mapToDouble(v -> {
                    VentaJuego vj = (VentaJuego) v;
                    return (vj.calcularSubtotal() - vj.calcularDescuento(vj.calcularSubtotal())) * vj.getIva();
                })
                .sum();

        double impuestoCafe = ventas.stream()
                .filter(v -> v instanceof VentaCafeteria)
                .mapToDouble(v -> ((VentaCafeteria) v).getImpuestoConsumo())
                .sum();

        return ivaJuegos + impuestoCafe;
    }

    public double getTotalPropinas(LocalDate desde, LocalDate hasta) {
        return filtrarVentas(desde, hasta).stream()
                .filter(v -> v instanceof VentaCafeteria)
                .mapToDouble(v -> ((VentaCafeteria) v).getPropina())
                .sum();
    }

    public double getTotalCostos(LocalDate desde, LocalDate hasta) {
        return filtrarVentas(desde, hasta).stream()
                .mapToDouble(Venta::calcularSubtotal)
                .sum();
    }

    // ==========================================================
    // CONSULTAS DE INVENTARIO (ADMIN)
    // ==========================================================

    public List<JuegoDeMesa> getInventarioPrestamo() {
        return cafe.getInventarioPrestamo().getJuegos();
    }

    public List<JuegoDeMesa> getInventarioPrestamoDisponible() {
        return cafe.getInventarioPrestamo().getDisponibles();
    }

    public List<JuegoDeMesa> getInventarioVenta() {
        return cafe.getInventarioVenta().getJuegos();
    }

    public List<Prestamo> getHistorialPrestamosPorJuego(JuegoDeMesa juego) {
        return cafe.getPrestamosPorJuego(juego);
    }

    // ==========================================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==========================================================

    private void otorgarPuntosFidelidad(Usuario usuario, double montoVenta) {
        if (usuario instanceof Cliente) {
            ((Cliente) usuario).agregarPuntos(montoVenta * 0.01);
        }
    }

    private List<Venta> filtrarVentas(LocalDate desde, LocalDate hasta) {
        return cafe.getVentas().stream()
                .filter(v -> {
                    LocalDate fecha = v.getFecha().toLocalDate();
                    return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
                })
                .collect(Collectors.toList());
    }
}
