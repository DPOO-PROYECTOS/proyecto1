package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cafe {

    // ===== ATRIBUTOS =====
    private String nombre;
    private int capacidadMaxima;
    private InventarioPrestamo inventarioPrestamo;
    private InventarioVenta inventarioVenta;
    private List<Mesa> mesas = new ArrayList<>();
    private List<ItemMenu> menu = new ArrayList<>();
    private PlanSemanal planSemanal;
    private List<Venta> ventas = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Prestamo> historialPrestamos = new ArrayList<>();
    private List<SolicitudCambioTurno> solicitudesCambioTurno = new ArrayList<>();
    private List<SugerenciaPlatillo> sugerencias = new ArrayList<>();



    // ===== CONSTRUCTOR =====
    public Cafe(String nombre, int capacidadMaxima) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.inventarioPrestamo = new InventarioPrestamo();
        this.inventarioVenta = new InventarioVenta();
        this.planSemanal = new PlanSemanal();
    }



    // ===== MÉTODOS =====
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public InventarioPrestamo getInventarioPrestamo() { return inventarioPrestamo; }
    public void setInventarioPrestamo(InventarioPrestamo inventarioPrestamo) { this.inventarioPrestamo = inventarioPrestamo; }

    public InventarioVenta getInventarioVenta() { return inventarioVenta; }
    public void setInventarioVenta(InventarioVenta inventarioVenta) { this.inventarioVenta = inventarioVenta; }

    public List<Mesa> getMesas() { return mesas; }
    public void setMesas(List<Mesa> mesas) { this.mesas = mesas; }

    public List<ItemMenu> getMenu() { return menu; }
    public void setMenu(List<ItemMenu> menu) { this.menu = menu; }

    public PlanSemanal getPlanSemanal() { return planSemanal; }
    public void setPlanSemanal(PlanSemanal planSemanal) { this.planSemanal = planSemanal; }

    public List<Venta> getVentas() { return ventas; }
    public void setVentas(List<Venta> ventas) { this.ventas = ventas; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<Prestamo> getHistorialPrestamos() { return historialPrestamos; }
    public void setHistorialPrestamos(List<Prestamo> historialPrestamos) { this.historialPrestamos = historialPrestamos; }

    public List<SolicitudCambioTurno> getSolicitudesCambioTurno() { return solicitudesCambioTurno; }
    public void setSolicitudesCambioTurno(List<SolicitudCambioTurno> solicitudesCambioTurno) { this.solicitudesCambioTurno = solicitudesCambioTurno; }

    public List<SugerenciaPlatillo> getSugerencias() { return sugerencias; }
    public void setSugerencias(List<SugerenciaPlatillo> sugerencias) { this.sugerencias = sugerencias; }


    public void agregarMesa(Mesa mesa) {
        mesas.add(mesa);
    }


    public void agregarItemMenu(ItemMenu item) {
        menu.add(item);
    }


    public void agregarVenta(Venta venta) {
        ventas.add(venta);
    }


    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }


    public void registrarPrestamo(Prestamo prestamo) {
        historialPrestamos.add(prestamo);
    }


    public int calcularPersonasActuales() {
        return mesas.stream()
                .filter(m -> !m.estaDisponible())
                .mapToInt(Mesa::getNumPersonas)
                .sum();
    }


    public boolean hayCapacidad(int personas) {
        return calcularPersonasActuales() + personas <= capacidadMaxima;
    }


    public Mesa buscarMesaDisponible(int personas) {
        return mesas.stream()
                .filter(m -> m.estaDisponible() && m.tieneCapacidadPara(personas))
                .findFirst()
                .orElse(null);
    }


    public Usuario buscarUsuarioPorLogin(String login) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);
    }


    public Empleado buscarEmpleadoPorCodigo(String codigoDescuento) {
        return usuarios.stream()
                .filter(u -> u instanceof Empleado)
                .map(u -> (Empleado) u)
                .filter(e -> e.getCodigoDescuento().equalsIgnoreCase(codigoDescuento))
                .findFirst()
                .orElse(null);
    }


    public List<Venta> getVentasPorUsuario(Usuario usuario) {
        return ventas.stream()
                .filter(v -> v.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }


    public List<Prestamo> getPrestamosPorJuego(JuegoDeMesa juego) {
        return historialPrestamos.stream()
                .filter(p -> p.getJuegos().contains(juego))
                .collect(Collectors.toList());
    }
}
