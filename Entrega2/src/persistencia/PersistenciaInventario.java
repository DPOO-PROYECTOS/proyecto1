package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.JuegoDeMesa;
import modelo.JuegoDificil;

public class PersistenciaInventario {

    public void cargarInventario(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jPrestamo = raiz.getJSONArray("inventarioPrestamo");
        for (int i = 0; i < jPrestamo.length(); i++) {
            JSONObject jJuego = jPrestamo.getJSONObject(i);

            String nombre = jJuego.getString("nombre");
            int anio = jJuego.getInt("anioPublicacion");
            String empresa = jJuego.getString("empresaMatriz");
            int minJ = jJuego.getInt("minJugadores");
            int maxJ = jJuego.getInt("maxJugadores");
            boolean aptaMenores = jJuego.getBoolean("aptaMenores5");
            boolean soloAdultos = jJuego.getBoolean("soloAdultos");
            String estado = jJuego.getString("estado");
            boolean disponible = jJuego.getBoolean("disponible");
            int vecesPrestado = jJuego.getInt("vecesPrestado");
            double precioVenta = jJuego.getDouble("precioVenta");
            String tipo = jJuego.getString("tipo");

            JuegoDeMesa juego = crearJuego(tipo, nombre, anio, empresa, minJ, maxJ, aptaMenores, soloAdultos, estado, disponible);
            if (juego != null) {
                juego.setVecesPrestado(vecesPrestado);
                juego.setPrecioVenta(precioVenta);
                cafe.getInventarioPrestamo().agregarJuego(juego);
            }
        }
    }

    private JuegoDeMesa crearJuego(String tipo, String nombre, int anio, String empresa,int minJ, int maxJ, boolean aptaMenores,boolean soloAdultos, String estado, boolean disponible) {
        if ("JuegoDificil".equals(tipo)) {
            return new JuegoDificil(nombre, anio, empresa, minJ, maxJ,aptaMenores, soloAdultos, estado, disponible);
        }
        return null;
    }
    public void salvarInventario(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jPrestamo = new JSONArray();
        for (JuegoDeMesa juego : cafe.getInventarioPrestamo().getJuegos()) {
            JSONObject jJuego = new JSONObject();
            jJuego.put("nombre",juego.getNombre());
            jJuego.put("anioPublicacion",juego.getAnioPublicacion());
            jJuego.put("empresaMatriz",juego.getEmpresaMatriz());
            jJuego.put("minJugadores",juego.getMinJugadores());
            jJuego.put("maxJugadores",juego.getMaxJugadores());
            jJuego.put("aptaMenores5",juego.isAptaMenores5());
            jJuego.put("soloAdultos",juego.isSoloAdultos());
            jJuego.put("estado",juego.getEstado());
            jJuego.put("disponible",juego.isDisponible());
            jJuego.put("vecesPrestado",juego.getVecesPrestado());
            jJuego.put("precioVenta",juego.getPrecioVenta());
            jJuego.put("tipo", juego.getClass().getSimpleName());
            jPrestamo.put(jJuego);
        }
        jobject.put("inventarioPrestamo", jPrestamo);
        JSONArray jVenta = new JSONArray();
        for (JuegoDeMesa juego : cafe.getInventarioVenta().getJuegos()) {
            JSONObject jJuego = new JSONObject();
            jJuego.put("nombre", juego.getNombre());
            jVenta.put(jJuego);
        }
        jobject.put("inventarioVenta", jVenta);

        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}