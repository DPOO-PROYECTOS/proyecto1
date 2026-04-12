package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cafe {

    // Attributes
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



    // Constructor
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
        int total = 0;
        for (Mesa m : mesas) {
            if (!m.estaDisponible()) {
                total += m.getNumPersonas();
            }
        }
        return total;
    }


    public boolean hayCapacidad(int personas) {
        return calcularPersonasActuales() + personas <= capacidadMaxima;
    }


    public Mesa buscarMesaDisponible(int personas) {
        for (Mesa m : mesas) {
            if (m.estaDisponible() && m.tieneCapacidadPara(personas)) {
                return m;
            }
        }
        return null;
    }


    public Usuario buscarUsuarioPorLogin(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equalsIgnoreCase(login)) {
                return u;
            }
        }
        return null;
    }


    public Empleado buscarEmpleadoPorCodigo(String codigoDescuento) {
        for (Usuario u : usuarios) {
            if (u instanceof Empleado) {
                Empleado e = (Empleado) u;
                if (e.getCodigoDescuento().equalsIgnoreCase(codigoDescuento)) {
                    return e;
                }
            }
        }
        return null;
    }


    public List<Venta> getVentasPorUsuario(Usuario usuario) {
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getUsuario().equals(usuario)) {
                resultado.add(v);
            }
        }
        return resultado;
    }


    public List<Prestamo> getPrestamosPorJuego(JuegoDeMesa juego) {
        List<Prestamo> resultado = new ArrayList<>();
        for (Prestamo p : historialPrestamos) {
            if (p.getJuegos().contains(juego)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
