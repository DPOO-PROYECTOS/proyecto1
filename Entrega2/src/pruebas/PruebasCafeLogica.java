package pruebas;

import logica.CafeLogica;
import modelo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebasCafeLogica {

    // estado compartido
    private Cafe cafe;
    private CafeLogica logica;

    private Cliente cliente;
    private Empleado cocinero;
    private Mesero mesero;
    private Admin admin;
    private Mesa mesa;
    private JuegoTablero juegoTablero;
    private Bebida cafeLatte;



    // setup

    @BeforeEach
    void setUp() {
        cafe = new Cafe("Cafe Ludico", 50);
        logica = new CafeLogica(cafe);

        // usuarios base
        cliente  = logica.registrarCliente("cliente1", "pass1");
        cocinero = logica.registrarEmpleado("cocinero1", "pass2", "cocinero");
        mesero   = (Mesero) logica.registrarEmpleado("mesero1", "pass3", "mesero");
        admin    = logica.registrarAdmin("admin1", "pass4");

        // mesa con capacidad para 4
        mesa = new Mesa(1, 4);
        logica.agregarMesa(mesa);

        // juego de tablero para 2-4 jugadores, apto menores
        juegoTablero = new JuegoTablero("Catan", 1995, "Catan GmbH", 2, 4, true, false, "bueno", true);
        logica.agregarJuegoInventarioPrestamo(juegoTablero);

        // item de menu
        cafeLatte = new Bebida("Cafe Latte", 5000, false, true);
        logica.agregarItemMenu(cafeLatte);
    }



    // usuarios

    @Test
    void loginExitoso() {
        Usuario u = logica.login("cliente1", "pass1");
        assertEquals(cliente, u);
    }

    @Test
    void loginUsuarioInexistente() {
        assertThrows(IllegalArgumentException.class, () -> logica.login("noexiste", "pass"));
    }

    @Test
    void loginPasswordIncorrecta() {
        assertThrows(IllegalArgumentException.class, () -> logica.login("cliente1", "wrong"));
    }

    @Test
    void registrarClienteLoginDuplicado() {
        assertThrows(IllegalArgumentException.class, () -> logica.registrarCliente("cliente1", "otra"));
    }

    @Test
    void registrarEmpleadoTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> logica.registrarEmpleado("emp2", "pass", "bartender"));
    }

    @Test
    void registrarEmpleadoMesero() {
        Empleado e = logica.registrarEmpleado("mesero2", "pass", "mesero");
        assertInstanceOf(Mesero.class, e);
    }

    @Test
    void registrarAdmin() {
        Admin a = logica.registrarAdmin("admin2", "pass");
        assertNotNull(a);
        assertEquals("admin2", a.getLogin());
    }



    // mesas

    @Test
    void buscarMesaPorNumeroExistente() {
        Mesa encontrada = logica.buscarMesaPorNumero(1);
        assertEquals(mesa, encontrada);
    }

    @Test
    void buscarMesaPorNumeroInexistente() {
        assertNull(logica.buscarMesaPorNumero(99));
    }

    @Test
    void asignarMesaExitoso() {
        Mesa asignada = logica.asignarMesa(cliente, 3, false, false);
        assertEquals(cliente, asignada.getClienteAsignado());
        assertEquals(3, asignada.getNumPersonas());
    }

    @Test
    void asignarMesaSinCapacidad() {
        // llenar el cafe
        Cafe cafePequeno = new Cafe("Pequeno", 2);
        CafeLogica logicaPequena = new CafeLogica(cafePequeno);
        Mesa mesaPequena = new Mesa(1, 4);
        logicaPequena.agregarMesa(mesaPequena);
        Cliente c = logicaPequena.registrarCliente("cTest", "p");
        logicaPequena.asignarMesa(c, 2, false, false);

        assertThrows(IllegalStateException.class, () -> logicaPequena.asignarMesa(c, 1, false, false));
    }

    @Test
    void asignarMesaNumPersonasCero() {
        assertThrows(IllegalArgumentException.class, () -> logica.asignarMesa(cliente, 0, false, false));
    }

    @Test
    void liberarMesa() {
        logica.asignarMesa(cliente, 2, false, false);
        assertFalse(mesa.estaDisponible());
        logica.liberarMesa(mesa);
        assertTrue(mesa.estaDisponible());
    }



    // prestamos

    @Test
    void solicitarPrestamoClienteExitoso() {
        logica.asignarMesa(cliente, 3, false, false);
        PrestamoCliente prestamo = logica.solicitarPrestamoCliente(cliente, "Catan");
        assertNotNull(prestamo);
        assertEquals(1, prestamo.getJuegos().size());
        assertTrue(prestamo.estaActivo());
    }

    @Test
    void solicitarPrestamoClienteSinMesa() {
        assertThrows(IllegalStateException.class, () -> logica.solicitarPrestamoCliente(cliente, "Catan"));
    }

    @Test
    void solicitarPrestamoClienteJuegoNoExiste() {
        logica.asignarMesa(cliente, 3, false, false);
        assertThrows(IllegalStateException.class, () -> logica.solicitarPrestamoCliente(cliente, "JuegoFicticio"));
    }

    @Test
    void solicitarPrestamoClienteMaxDosJuegos() {
        // segundo juego para el inventario
        JuegoTablero juego2 = new JuegoTablero("Carcassonne", 2000, "Hans im Gluck", 2, 5, true, false, "bueno", true);
        logica.agregarJuegoInventarioPrestamo(juego2);
        JuegoTablero juego3 = new JuegoTablero("Ticket to Ride", 2004, "Days of Wonder", 2, 5, true, false, "bueno", true);
        logica.agregarJuegoInventarioPrestamo(juego3);

        logica.asignarMesa(cliente, 3, false, false);
        logica.solicitarPrestamoCliente(cliente, "Catan");
        logica.solicitarPrestamoCliente(cliente, "Carcassonne");

        assertThrows(IllegalStateException.class, () -> logica.solicitarPrestamoCliente(cliente, "Ticket to Ride"));
    }

    @Test
    void getPrestamoActivoCliente() {
        logica.asignarMesa(cliente, 3, false, false);
        logica.solicitarPrestamoCliente(cliente, "Catan");
        PrestamoCliente activo = logica.getPrestamoActivoCliente(cliente);
        assertNotNull(activo);
        assertTrue(activo.estaActivo());
    }

    @Test
    void devolverJuego() {
        logica.asignarMesa(cliente, 3, false, false);
        PrestamoCliente prestamo = logica.solicitarPrestamoCliente(cliente, "Catan");
        logica.devolverJuego(prestamo, juegoTablero);
        assertTrue(prestamo.getJuegos().isEmpty());
        assertFalse(prestamo.estaActivo());
    }

    @Test
    void devolverJuegoQueNoPertenece() {
        logica.asignarMesa(cliente, 3, false, false);
        PrestamoCliente prestamo = logica.solicitarPrestamoCliente(cliente, "Catan");

        JuegoTablero otroJuego = new JuegoTablero("Otro", 2000, "X", 2, 4, true, false, "bueno", true);
        assertThrows(IllegalArgumentException.class, () -> logica.devolverJuego(prestamo, otroJuego));
    }

    @Test
    void getPrestamosActivos() {
        logica.asignarMesa(cliente, 3, false, false);
        logica.solicitarPrestamoCliente(cliente, "Catan");
        List<Prestamo> activos = logica.getPrestamosActivos();
        assertEquals(1, activos.size());
    }



    // ventas cafeteria

    @Test
    void realizarPedidoCafeExitoso() {
        logica.asignarMesa(cliente, 2, false, false);
        VentaCafeteria venta = logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);
        assertNotNull(venta);
        assertEquals(1, cafe.getVentas().size());
    }

    @Test
    void realizarPedidoCafeAdminNoPermitido() {
        logica.asignarMesa(cliente, 2, false, false);
        assertThrows(IllegalStateException.class, () ->
            logica.realizarPedidoCafe(admin, mesa, Arrays.asList(cafeLatte), 0, 0));
    }

    @Test
    void realizarPedidoCafeSinItems() {
        logica.asignarMesa(cliente, 2, false, false);
        assertThrows(IllegalArgumentException.class, () ->
            logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(), 0, 0));
    }

    @Test
    void realizarPedidoCafeAlcoholConMenores() {
        logica.asignarMesa(cliente, 2, true, false); // con ninos
        Bebida cerveza = new Bebida("Cerveza", 8000, true, false);
        logica.agregarItemMenu(cerveza);
        assertThrows(IllegalStateException.class, () ->
            logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cerveza), 0, 0));
    }

    @Test
    void pedidoOtorgaPuntosFidelidad() {
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);
        assertTrue(cliente.getPuntosFidelidad() > 0);
    }



    // ventas juego

    @Test
    void venderJuegosExitoso() {
        JuegoTablero juegoVenta = new JuegoTablero("Chess", 1900, "FIDE", 2, 2, true, false, "bueno", true);
        logica.agregarJuegoInventarioVenta(juegoVenta, 30000);

        VentaJuego venta = logica.venderJuegos(cliente, Arrays.asList(juegoVenta), null, 0);
        assertNotNull(venta);
        assertEquals(1, cafe.getVentas().size());
        assertFalse(cafe.getInventarioVenta().estaEnInventario(juegoVenta));
    }

    @Test
    void venderJuegosAdminNoPermitido() {
        JuegoTablero juegoVenta = new JuegoTablero("Chess", 1900, "FIDE", 2, 2, true, false, "bueno", true);
        logica.agregarJuegoInventarioVenta(juegoVenta, 30000);
        assertThrows(IllegalStateException.class, () ->
            logica.venderJuegos(admin, Arrays.asList(juegoVenta), null, 0));
    }

    @Test
    void venderJuegoFueraDeInventario() {
        JuegoTablero juegoFuera = new JuegoTablero("NoStock", 2000, "X", 2, 4, true, false, "bueno", true);
        assertThrows(IllegalStateException.class, () ->
            logica.venderJuegos(cliente, Arrays.asList(juegoFuera), null, 0));
    }

    @Test
    void venderJuegosConCodigoDescuentoValido() {
        JuegoTablero juegoVenta = new JuegoTablero("Chess", 1900, "FIDE", 2, 2, true, false, "bueno", true);
        logica.agregarJuegoInventarioVenta(juegoVenta, 30000);
        String codigo = cocinero.getCodigoDescuento();
        VentaJuego venta = logica.venderJuegos(cliente, Arrays.asList(juegoVenta), codigo, 0);
        assertNotNull(venta.getEmpleadoDescuento());
    }

    @Test
    void venderJuegosConCodigoDescuentoInvalido() {
        JuegoTablero juegoVenta = new JuegoTablero("Chess", 1900, "FIDE", 2, 2, true, false, "bueno", true);
        logica.agregarJuegoInventarioVenta(juegoVenta, 30000);
        assertThrows(IllegalArgumentException.class, () ->
            logica.venderJuegos(cliente, Arrays.asList(juegoVenta), "INVALIDO", 0));
    }



    // inventario

    @Test
    void agregarJuegoInventarioPrestamoDisponible() {
        JuegoTablero nuevo = new JuegoTablero("Dixit", 2008, "Libellud", 3, 6, true, false, "bueno", false);
        logica.agregarJuegoInventarioPrestamo(nuevo);
        assertTrue(nuevo.isDisponible());
        assertTrue(logica.getInventarioPrestamo().contains(nuevo));
    }

    @Test
    void agregarJuegoInventarioVenta() {
        JuegoTablero nuevo = new JuegoTablero("Dixit", 2008, "Libellud", 3, 6, true, false, "bueno", false);
        logica.agregarJuegoInventarioVenta(nuevo, 25000);
        assertEquals(25000, nuevo.getPrecioVenta());
        assertTrue(logica.getInventarioVenta().contains(nuevo));
    }

    @Test
    void moverDeVentaAPrestamo() {
        JuegoTablero juego = new JuegoTablero("Pandemic", 2008, "Z-Man", 2, 4, false, false, "bueno", false);
        logica.agregarJuegoInventarioVenta(juego, 40000);
        logica.moverDeVentaAPrestamo(juego);
        assertTrue(logica.getInventarioPrestamo().contains(juego));
        assertFalse(logica.getInventarioVenta().contains(juego));
        assertTrue(juego.isDisponible());
    }

    @Test
    void moverJuegoQueNoEstaEnVenta() {
        JuegoTablero juego = new JuegoTablero("Pandemic", 2008, "Z-Man", 2, 4, false, false, "bueno", true);
        assertThrows(IllegalStateException.class, () -> logica.moverDeVentaAPrestamo(juego));
    }

    @Test
    void marcarComoRobado() {
        logica.marcarComoRobado(juegoTablero);
        assertEquals("desaparecido", juegoTablero.getEstado());
        assertFalse(juegoTablero.isDisponible());
        assertFalse(logica.getInventarioPrestamo().contains(juegoTablero));
    }



    // turnos

    @Test
    void crearTurnoExitoso() {
        // necesitamos 2 meseros y 1 cocinero minimo para cumplir regla
        Mesero mesero2 = (Mesero) logica.registrarEmpleado("mesero2", "pass", "mesero");
        logica.crearTurno(cocinero, "Lunes", "08:00", "16:00");
        logica.crearTurno(mesero, "Lunes", "08:00", "16:00");
        Turno t = logica.crearTurno(mesero2, "Lunes", "08:00", "16:00");
        assertNotNull(t);
        assertTrue(mesero2.isEnTurno());
    }

    @Test
    void eliminarTurnoViolaMinimosFalla() {
        // solo hay 1 mesero y 1 cocinero -> eliminar cualquiera viola el minimo (necesita 2 meseros)
        Turno turnoCocinero = logica.crearTurno(cocinero, "Martes", "08:00", "16:00");
        Turno turnoMesero  = logica.crearTurno(mesero,   "Martes", "08:00", "16:00");
        // solo 1 mesero en Martes -> eliminar viola el minimo de 2 meseros
        assertThrows(IllegalStateException.class, () -> logica.eliminarTurno(turnoMesero));
    }

    @Test
    void solicitarCambioTurnoExitoso() {
        Mesero mesero2 = (Mesero) logica.registrarEmpleado("mesero2", "pass", "mesero");
        logica.crearTurno(cocinero, "Miercoles", "08:00", "16:00");
        Turno turnoM1 = logica.crearTurno(mesero,   "Miercoles", "08:00", "16:00");
        logica.crearTurno(mesero2,  "Miercoles", "08:00", "16:00");

        // mesero solicita intercambio con mesero2
        SolicitudCambioTurno solicitud = logica.solicitarCambioTurno(mesero, turnoM1, mesero2);
        assertNotNull(solicitud);
        assertEquals("pendiente", solicitud.getEstado());
    }

    @Test
    void solicitarCambioTurnoEmpleadoEquivocado() {
        logica.crearTurno(cocinero, "Jueves", "08:00", "16:00");
        Turno turnoMesero = logica.crearTurno(mesero, "Jueves", "08:00", "16:00");
        assertThrows(IllegalArgumentException.class, () ->
            logica.solicitarCambioTurno(cocinero, turnoMesero, null));
    }

    @Test
    void aprobarCambioTurno() {
        Mesero mesero2 = (Mesero) logica.registrarEmpleado("mesero2", "pass", "mesero");
        logica.crearTurno(cocinero, "Viernes", "08:00", "16:00");
        Turno turnoM1 = logica.crearTurno(mesero,  "Viernes", "08:00", "16:00");
        logica.crearTurno(mesero2,  "Viernes", "08:00", "16:00");

        SolicitudCambioTurno solicitud = logica.solicitarCambioTurno(mesero, turnoM1, mesero2);
        logica.aprobarCambioTurno(admin, solicitud);
        assertEquals("aprobada", solicitud.getEstado());
        // los empleados del turno deben haberse intercambiado
        assertEquals(mesero2, turnoM1.getEmpleado());
    }

    @Test
    void rechazarCambioTurno() {
        Mesero mesero2 = (Mesero) logica.registrarEmpleado("mesero2", "pass", "mesero");
        logica.crearTurno(cocinero, "Sabado", "08:00", "16:00");
        Turno turnoM1 = logica.crearTurno(mesero,  "Sabado", "08:00", "16:00");
        logica.crearTurno(mesero2,  "Sabado", "08:00", "16:00");

        SolicitudCambioTurno solicitud = logica.solicitarCambioTurno(mesero, turnoM1, mesero2);
        logica.rechazarCambioTurno(admin, solicitud);
        assertEquals("rechazada", solicitud.getEstado());
    }

    @Test
    void getSolicitudesPendientes() {
        Mesero mesero2 = (Mesero) logica.registrarEmpleado("mesero2", "pass", "mesero");
        logica.crearTurno(cocinero, "Domingo", "08:00", "16:00");
        Turno turnoM1 = logica.crearTurno(mesero,  "Domingo", "08:00", "16:00");
        logica.crearTurno(mesero2,  "Domingo", "08:00", "16:00");

        logica.solicitarCambioTurno(mesero, turnoM1, mesero2);
        assertEquals(1, logica.getSolicitudesPendientes().size());
    }



    // menu

    @Test
    void agregarItemMenuExitoso() {
        Bebida te = new Bebida("Te", 3000, false, true);
        logica.agregarItemMenu(te);
        assertTrue(cafe.getMenu().contains(te));
    }

    @Test
    void sugerirPlatilloYAprobar() {
        Bebida nuevo = new Bebida("Matcha", 6000, false, true);
        SugerenciaPlatillo sugerencia = logica.sugerirPlatillo(mesero, "Bebida saludable", nuevo);
        assertNotNull(sugerencia);
        assertEquals(1, logica.getSugerenciasPendientes().size());

        logica.aprobarSugerencia(admin, sugerencia);
        assertTrue(sugerencia.isAprobada());
        assertTrue(cafe.getMenu().contains(nuevo));
        assertEquals(0, logica.getSugerenciasPendientes().size());
    }

    @Test
    void sugerirPlatilloYRechazar() {
        Bebida nuevo = new Bebida("Matcha", 6000, false, true);
        SugerenciaPlatillo sugerencia = logica.sugerirPlatillo(mesero, "Bebida saludable", nuevo);
        logica.rechazarSugerencia(admin, sugerencia);
        assertFalse(sugerencia.isAprobada());
        assertFalse(cafe.getMenu().contains(nuevo));
    }



    // favoritos

    @Test
    void agregarYQuitarFavorito() {
        logica.agregarFavorito(cliente, juegoTablero);
        assertTrue(cliente.getFavoritos().contains(juegoTablero));

        logica.quitarFavorito(cliente, juegoTablero);
        assertFalse(cliente.getFavoritos().contains(juegoTablero));
    }

    @Test
    void adminNoPuedeAgregarFavorito() {
        assertThrows(IllegalStateException.class, () -> logica.agregarFavorito(admin, juegoTablero));
    }



    // reportes

    @Test
    void getTotalVentasDia() {
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);

        double total = logica.getTotalVentasDia(LocalDate.now());
        assertTrue(total > 0);
    }

    @Test
    void getTotalVentasSemana() {
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);

        double total = logica.getTotalVentasSemana(LocalDate.now().minusDays(1));
        assertTrue(total > 0);
    }

    @Test
    void getTotalVentasMes() {
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);

        LocalDate hoy = LocalDate.now();
        double total = logica.getTotalVentasMes(hoy.getMonthValue(), hoy.getYear());
        assertTrue(total > 0);
    }

    @Test
    void getTotalVentasJuegosYCafe() {
        // venta cafeteria
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 0, 0);

        // venta juego
        JuegoTablero jVenta = new JuegoTablero("Chess", 1900, "FIDE", 2, 2, true, false, "bueno", true);
        logica.agregarJuegoInventarioVenta(jVenta, 30000);
        logica.venderJuegos(cliente, Arrays.asList(jVenta), null, 0);

        LocalDate hoy = LocalDate.now();
        double totalJuegos = logica.getTotalVentasJuegos(hoy, hoy);
        double totalCafe   = logica.getTotalVentasCafe(hoy, hoy);

        assertTrue(totalJuegos > 0);
        assertTrue(totalCafe > 0);
    }

    @Test
    void getTotalImpuestosYPropinas() {
        logica.asignarMesa(cliente, 2, false, false);
        logica.realizarPedidoCafe(cliente, mesa, Arrays.asList(cafeLatte), 500, 0);

        LocalDate hoy = LocalDate.now();
        double impuestos = logica.getTotalImpuestos(hoy, hoy);
        double propinas  = logica.getTotalPropinas(hoy, hoy);

        assertTrue(impuestos > 0);
        assertEquals(500, propinas, 0.01);
    }
}
