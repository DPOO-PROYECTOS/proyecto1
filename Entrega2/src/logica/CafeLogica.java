package logica;

import modelo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CafeLogica {

    // Attributes
    private Cafe cafe;



    // Constructor
    public CafeLogica(Cafe cafe) {
        this.cafe = cafe;
    }

    public Cafe getCafe() { return cafe; }



    // usuarios

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
            throw new IllegalArgumentException("Tipo inválido: '" + tipo + "'. Use 'mesero' o 'cocinero'.");
        }
        cafe.agregarUsuario(empleado);
        return empleado;
    }

    public Admin registrarAdmin(String login, String password) {
        if (cafe.buscarUsuarioPorLogin(login) != null)
            throw new IllegalArgumentException("El login '" + login + "' ya está en uso.");
        Admin admin = new Admin(login, password);
        cafe.agregarUsuario(admin);
        return admin;
    }



    // mesas

    public void agregarMesa(Mesa mesa) {
        cafe.agregarMesa(mesa);
    }

    public Mesa buscarMesaPorNumero(int numero) {
        for (Mesa m : cafe.getMesas()) {
            if (m.getNumero() == numero) {
                return m;
            }
        }
        return null;
    }

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



    // prestamos

    public PrestamoCliente solicitarPrestamoCliente(Cliente cliente, String nombreJuego) {
        Mesa mesa = getMesaDeCliente(cliente);
        if (mesa == null)
            throw new IllegalStateException("El cliente no tiene una mesa asignada.");

        PrestamoCliente prestamoActivo = getPrestamoActivoCliente(cliente);
        if (prestamoActivo != null && prestamoActivo.getJuegos().size() >= 2)
            throw new IllegalStateException("El cliente ya tiene 2 juegos en préstamo (máximo permitido).");

        JuegoDeMesa juego = cafe.getInventarioPrestamo().buscarDisponiblePorNombre(nombreJuego);
        if (juego == null) {
            JuegoDeMesa existente = cafe.getInventarioPrestamo().buscarPorNombre(nombreJuego);
            if (existente != null)
                throw new IllegalStateException("El juego '" + nombreJuego + "' está actualmente prestado.");
            throw new IllegalStateException("El juego '" + nombreJuego + "' no se encuentra en el inventario.");
        }

        if (!juego.esAptoParaMesa(mesa.getNumPersonas(), mesa.isTieneNinos(), mesa.isTieneJovenes()))
            throw new IllegalStateException("El juego '" + nombreJuego + "' no es apto para esta mesa.");

        if (juego instanceof JuegoDeAccion && mesa.tieneBebidaCaliente())
            throw new IllegalStateException("No se puede prestar un juego de Acción a una mesa con bebidas calientes.");

        if (juego instanceof JuegoDificil) {
            if (!hayMeseroDisponibleParaJuegoDificil((JuegoDificil) juego))
                System.out.println("Advertencia: no hay mesero capacitado para '" + nombreJuego + "'.");
            else
                System.out.println("Hay un mesero disponible para introducir el juego '" + nombreJuego + "'.");
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
            throw new IllegalStateException("El empleado está en turno y hay clientes que atender.");

        JuegoDeMesa juego = cafe.getInventarioPrestamo().buscarDisponiblePorNombre(nombreJuego);
        if (juego == null) {
            JuegoDeMesa existente = cafe.getInventarioPrestamo().buscarPorNombre(nombreJuego);
            if (existente != null)
                throw new IllegalStateException("El juego '" + nombreJuego + "' está actualmente prestado.");
            throw new IllegalStateException("El juego '" + nombreJuego + "' no se encuentra en el inventario.");
        }

        if (juego instanceof JuegoDificil) {
            if (!hayMeseroDisponibleParaJuegoDificil((JuegoDificil) juego))
                System.out.println("Advertencia: no hay mesero capacitado para '" + nombreJuego + "'.");
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
        for (Prestamo p : cafe.getHistorialPrestamos()) {
            if (p instanceof PrestamoCliente) {
                PrestamoCliente pc = (PrestamoCliente) p;
                if (pc.getCliente().equals(cliente) && pc.estaActivo()) {
                    return pc;
                }
            }
        }
        return null;
    }

    public List<Prestamo> getPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : cafe.getHistorialPrestamos()) {
            if (p.estaActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }



    // ventas cafeteria

    public VentaCafeteria realizarPedidoCafe(Usuario usuario, Mesa mesa,
                                              List<ItemMenu> items, double propina,
                                              double puntosAUsar) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede realizar pedidos.");
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

        if (propina < 0) {
            venta.setPropina(venta.calcularPropinaSugerida());
        } else {
            venta.setPropina(propina);
        }

        if (puntosAUsar > 0) {
            if (!(usuario instanceof Cliente))
                throw new IllegalStateException("Solo los clientes pueden usar puntos de fidelidad.");
            if (!((Cliente) usuario).usarPuntos(puntosAUsar))
                throw new IllegalStateException("Puntos insuficientes. Disponibles: " + ((Cliente) usuario).getPuntosFidelidad());
        }

        for (ItemMenu item : items) {
            if (item instanceof Bebida) {
                mesa.agregarBebida((Bebida) item);
            }
        }

        otorgarPuntosFidelidad(usuario, venta.calcularSubtotal());
        cafe.agregarVenta(venta);
        return venta;
    }



    // ventas juego

    public VentaJuego venderJuegos(Usuario usuario, List<JuegoDeMesa> juegos,
                                    String codigoDescuento, double puntosAUsar) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede realizar compras.");
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
                throw new IllegalStateException("El juego '" + juego.getNombre() + "' no está en el inventario de ventas.");
            venta.agregarLinea(new LineaVenta(1, juego.getPrecioVenta(), juego));
        }

        if (puntosAUsar > 0) {
            if (!(usuario instanceof Cliente))
                throw new IllegalStateException("Solo los clientes pueden usar puntos de fidelidad.");
            if (!((Cliente) usuario).usarPuntos(puntosAUsar))
                throw new IllegalStateException("Puntos insuficientes. Disponibles: " + ((Cliente) usuario).getPuntosFidelidad());
        }

        for (JuegoDeMesa juego : juegos) {
            cafe.getInventarioVenta().quitarJuego(juego);
        }

        otorgarPuntosFidelidad(usuario, venta.calcularTotal());
        cafe.agregarVenta(venta);
        return venta;
    }



    // inventario

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
            throw new IllegalStateException("El juego '" + juego.getNombre() + "' no está en el inventario de ventas.");
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



    // turnos

    public Turno crearTurno(Empleado empleado, String dia, String horaInicio, String horaFin) {
        Turno turno = new Turno(dia, horaInicio, horaFin, empleado);
        cafe.getPlanSemanal().agregarTurno(turno);
        empleado.setEnTurno(true);
        return turno;
    }

    public void eliminarTurno(Turno turno) {
        String dia = turno.getDia();
        PlanSemanal plan = cafe.getPlanSemanal();
        plan.quitarTurno(turno);
        if (!plan.cumpleMinimosDia(dia)) {
            plan.agregarTurno(turno);
            throw new IllegalStateException("No se puede eliminar: el día " + dia + " quedaría sin el mínimo de empleados.");
        }
        turno.getEmpleado().setEnTurno(false);
    }

    public void modificarTurno(Turno turno, String nuevoDia, String nuevaHoraInicio, String nuevaHoraFin) {
        String diaOriginal = turno.getDia();
        String horaInicioOriginal = turno.getHoraInicio();
        String horaFinOriginal = turno.getHoraFin();

        turno.setDia(nuevoDia);
        turno.setHoraInicio(nuevaHoraInicio);
        turno.setHoraFin(nuevaHoraFin);

        if (!cafe.getPlanSemanal().cumpleMinimosDia(diaOriginal)) {
            turno.setDia(diaOriginal);
            turno.setHoraInicio(horaInicioOriginal);
            turno.setHoraFin(horaFinOriginal);
            throw new IllegalStateException("La modificación dejaría el día " + diaOriginal + " sin el mínimo de empleados.");
        }
    }

    public SolicitudCambioTurno solicitarCambioTurno(Empleado solicitante, Turno turno, Empleado intercambiarCon) {
        if (!turno.getEmpleado().equals(solicitante))
            throw new IllegalArgumentException("El turno no pertenece al empleado solicitante.");

        if (intercambiarCon == null) {
            PlanSemanal plan = cafe.getPlanSemanal();
            plan.quitarTurno(turno);
            boolean cumple = plan.cumpleMinimosDia(turno.getDia());
            plan.agregarTurno(turno);
            if (!cumple)
                throw new IllegalStateException("El día " + turno.getDia() + " quedaría sin el mínimo de empleados.");
        }

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turno, solicitante, intercambiarCon);
        cafe.agregarSolicitudCambioTurno(solicitud);
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
            Turno turnoOtro = null;
            for (Turno t : cafe.getPlanSemanal().getTurnosDia(turno.getDia())) {
                if (t.getEmpleado().equals(intercambiarCon)) {
                    turnoOtro = t;
                    break;
                }
            }
            turno.setEmpleado(intercambiarCon);
            if (turnoOtro != null) {
                turnoOtro.setEmpleado(solicitante);
            }
        } else {
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
        List<SolicitudCambioTurno> pendientes = new ArrayList<>();
        for (SolicitudCambioTurno s : cafe.getSolicitudesCambioTurno()) {
            if ("pendiente".equals(s.getEstado())) {
                pendientes.add(s);
            }
        }
        return pendientes;
    }

    public List<Turno> getTurnosDeEmpleado(Empleado empleado) {
        return cafe.getPlanSemanal().getTurnosDeEmpleado(empleado);
    }



    // menu

    public void agregarItemMenu(ItemMenu item) {
        cafe.agregarItemMenu(item);
    }

    public void removerItemMenu(ItemMenu item) {
        cafe.getMenu().remove(item);
    }

    public SugerenciaPlatillo sugerirPlatillo(Empleado empleado, String descripcion, ItemMenu item) {
        SugerenciaPlatillo sugerencia = new SugerenciaPlatillo(descripcion, item, empleado);
        cafe.agregarSugerencia(sugerencia);
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
        List<SugerenciaPlatillo> pendientes = new ArrayList<>();
        for (SugerenciaPlatillo s : cafe.getSugerencias()) {
            if (!s.isAprobada() && s.getAdmin() == null) {
                pendientes.add(s);
            }
        }
        return pendientes;
    }



    // favoritos

    public void agregarFavorito(Usuario usuario, JuegoDeMesa juego) {
        if (usuario instanceof Admin)
            throw new IllegalStateException("El administrador no puede gestionar favoritos.");
        usuario.agregarFavorito(juego);
    }

    public void quitarFavorito(Usuario usuario, JuegoDeMesa juego) {
        usuario.quitarFavorito(juego);
    }



    // reportes

    public double getTotalVentasDia(LocalDate fecha) {
        double total = 0;
        for (Venta v : filtrarVentas(fecha, fecha)) {
            total += v.calcularTotal();
        }
        return total;
    }

    public double getTotalVentasSemana(LocalDate inicioSemana) {
        double total = 0;
        for (Venta v : filtrarVentas(inicioSemana, inicioSemana.plusDays(6))) {
            total += v.calcularTotal();
        }
        return total;
    }

    public double getTotalVentasMes(int mes, int anio) {
        double total = 0;
        for (Venta v : cafe.getVentas()) {
            if (v.getFecha().getMonthValue() == mes && v.getFecha().getYear() == anio) {
                total += v.calcularTotal();
            }
        }
        return total;
    }

    public double getTotalVentasJuegos(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Venta v : filtrarVentas(desde, hasta)) {
            if (v instanceof VentaJuego) {
                total += v.calcularTotal();
            }
        }
        return total;
    }

    public double getTotalVentasCafe(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Venta v : filtrarVentas(desde, hasta)) {
            if (v instanceof VentaCafeteria) {
                total += v.calcularTotal();
            }
        }
        return total;
    }

    public double getTotalImpuestos(LocalDate desde, LocalDate hasta) {
        double ivaJuegos = 0;
        double impuestoCafe = 0;
        for (Venta v : filtrarVentas(desde, hasta)) {
            if (v instanceof VentaJuego) {
                VentaJuego vj = (VentaJuego) v;
                ivaJuegos += (vj.calcularSubtotal() - vj.calcularDescuento(vj.calcularSubtotal())) * vj.getIva();
            } else if (v instanceof VentaCafeteria) {
                impuestoCafe += ((VentaCafeteria) v).getImpuestoConsumo();
            }
        }
        return ivaJuegos + impuestoCafe;
    }

    public double getTotalPropinas(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Venta v : filtrarVentas(desde, hasta)) {
            if (v instanceof VentaCafeteria) {
                total += ((VentaCafeteria) v).getPropina();
            }
        }
        return total;
    }

    public double getTotalCostos(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Venta v : filtrarVentas(desde, hasta)) {
            total += v.calcularSubtotal();
        }
        return total;
    }



    // metodos privados

    private void otorgarPuntosFidelidad(Usuario usuario, double montoVenta) {
        if (usuario instanceof Cliente) {
            ((Cliente) usuario).agregarPuntos(montoVenta * 0.01);
        }
    }

    private List<Venta> filtrarVentas(LocalDate desde, LocalDate hasta) {
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : cafe.getVentas()) {
            LocalDate fecha = v.getFecha().toLocalDate();
            if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    private Mesa getMesaDeCliente(Cliente cliente) {
        for (Mesa m : cafe.getMesas()) {
            if (cliente.equals(m.getClienteAsignado())) {
                return m;
            }
        }
        return null;
    }

    private boolean hayMeseroDisponibleParaJuegoDificil(JuegoDificil juego) {
        for (Usuario u : cafe.getUsuarios()) {
            if (u instanceof Mesero) {
                if (((Mesero) u).conoceJuego(juego)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hayClientesEnElCafe() {
        for (Mesa m : cafe.getMesas()) {
            if (!m.estaDisponible()) {
                return true;
            }
        }
        return false;
    }

    private void validarItemParaMesa(ItemMenu item, Mesa mesa) {
        if (item instanceof Bebida) {
            Bebida bebida = (Bebida) item;
            if (bebida.isEsAlcoholica() && mesa.tieneMenores())
                throw new IllegalStateException("No se puede servir '" + bebida.getNombre() + "' a una mesa con menores.");
            if (bebida.isEsCaliente()) {
                PrestamoCliente prestamo = getPrestamoActivoCliente(mesa.getClienteAsignado());
                if (prestamo != null) {
                    for (JuegoDeMesa j : prestamo.getJuegos()) {
                        if (j instanceof JuegoDeAccion)
                            throw new IllegalStateException("No se puede servir bebida caliente a una mesa con un juego de Acción activo.");
                    }
                }
            }
        }
        if (item instanceof Pasteleria) {
            Pasteleria p = (Pasteleria) item;
            if (!p.getAlergenos().isEmpty()) {
                System.out.println("Advertencia de alérgenos en '" + p.getNombre() + "': " + String.join(", ", p.getAlergenos()));
            }
        }
    }
}
