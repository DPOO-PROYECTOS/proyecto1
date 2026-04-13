package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.Empleado;
import modelo.ItemMenu;
import modelo.JuegoDeMesa;
import modelo.LineaVenta;
import modelo.Usuario;
import modelo.Venta;
import modelo.VentaCafeteria;
import modelo.VentaJuego;

public class PersistenciaVentas {

    public void cargarVentas(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jVentas = raiz.getJSONArray("ventas");
        for (int i = 0; i < jVentas.length(); i++) {
            JSONObject jVenta = jVentas.getJSONObject(i);
            String tipo        = jVenta.getString("tipo");
            LocalDateTime fecha = LocalDateTime.parse(jVenta.getString("fecha"));
            String loginUsuario = jVenta.getString("loginUsuario");

            Usuario usuario = cafe.buscarUsuarioPorLogin(loginUsuario);
            if (usuario == null) continue;

            // Creamos la subclase correcta
            Venta venta;
            if ("VentaCafeteria".equals(tipo)) {
                VentaCafeteria vc = new VentaCafeteria(fecha, usuario);
                vc.setPropina(jVenta.getDouble("propina"));
                venta = vc;
            } else if ("VentaJuego".equals(tipo)) {
                venta = new VentaJuego(fecha, usuario);
            } else continue;

            // Empleado descuento (puede ser null)
            if (!jVenta.isNull("loginEmpleadoDescuento")) {
                Usuario u = cafe.buscarUsuarioPorLogin(jVenta.getString("loginEmpleadoDescuento"));
                if (u instanceof Empleado) {
                    venta.setEmpleadoDescuento((Empleado) u);
                }
            }

            // Lineas de venta
            JSONArray jLineas = jVenta.getJSONArray("lineas");
            for (int j = 0; j < jLineas.length(); j++) {
                JSONObject jLinea = jLineas.getJSONObject(j);
                int cantidad          = jLinea.getInt("cantidad");
                double precioUnitario = jLinea.getDouble("precioUnitario");
                String tipoLinea      = jLinea.getString("tipoLinea"); // "juego" o "item"

                if ("juego".equals(tipoLinea)) {
                    String nombreJuego = jLinea.getString("nombre");
                    JuegoDeMesa juego = cafe.getInventarioVenta().buscarPorNombre(nombreJuego);
                    if (juego != null) {
                        venta.agregarLinea(new LineaVenta(cantidad, precioUnitario, juego));
                    }
                } else if ("item".equals(tipoLinea)) {
                    String nombreItem = jLinea.getString("nombre");
                    ItemMenu item = buscarItemMenu(cafe, nombreItem);
                    if (item != null) {
                        venta.agregarLinea(new LineaVenta(cantidad, precioUnitario, item));
                    }
                }
            }
            cafe.agregarVenta(venta);
        }
    }

    private ItemMenu buscarItemMenu(Cafe cafe, String nombre) {
        for (ItemMenu item : cafe.getMenu()) {
            if (item.getNombre().equalsIgnoreCase(nombre)) return item;
        }
        return null;
    }

    public void salvarVentas(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jVentas = new JSONArray();
        for (Venta venta : cafe.getVentas()) {
            JSONObject jVenta = new JSONObject();
            jVenta.put("tipo",        venta.getClass().getSimpleName());
            jVenta.put("fecha",       venta.getFecha().toString());
            jVenta.put("loginUsuario", venta.getUsuario().getLogin());
            jVenta.put("loginEmpleadoDescuento", venta.getEmpleadoDescuento() != null
                    ? venta.getEmpleadoDescuento().getLogin()
                    : JSONObject.NULL);

            // Propina solo existe en VentaCafeteria
            if (venta instanceof VentaCafeteria) {
                jVenta.put("propina", ((VentaCafeteria) venta).getPropina());
            }

            // Lineas
            JSONArray jLineas = new JSONArray();
            for (LineaVenta linea : venta.getLineas()) {
                JSONObject jLinea = new JSONObject();
                jLinea.put("cantidad",       linea.getCantidad());
                jLinea.put("precioUnitario", linea.getPrecioUnitario());
                if (linea.esDeJuego()) {
                    jLinea.put("tipoLinea", "juego");
                    jLinea.put("nombre",    linea.getJuego().getNombre());
                } else {
                    jLinea.put("tipoLinea", "item");
                    jLinea.put("nombre",    linea.getItem().getNombre());
                }
                jLineas.put(jLinea);
            }
            jVenta.put("lineas", jLineas);
            jVentas.put(jVenta);
        }
        jobject.put("ventas", jVentas);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}