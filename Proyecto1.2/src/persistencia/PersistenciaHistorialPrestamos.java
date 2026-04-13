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
import modelo.Cliente;
import modelo.Empleado;
import modelo.JuegoDeMesa;
import modelo.Mesa;
import modelo.Prestamo;
import modelo.PrestamoCliente;
import modelo.PrestamoEmpleado;
import modelo.Usuario;

public class PersistenciaHistorialPrestamos {

    public void cargarPrestamos(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jPrestamos = raiz.getJSONArray("historialPrestamos");
        for (int i = 0; i < jPrestamos.length(); i++) {
            JSONObject jPrestamo = jPrestamos.getJSONObject(i);
            String tipo           = jPrestamo.getString("tipo");
            LocalDateTime inicio  = LocalDateTime.parse(jPrestamo.getString("fechaInicio"));

            Prestamo prestamo = null;

            if ("PrestamoCliente".equals(tipo)) {
                String loginCliente = jPrestamo.getString("loginCliente");
                int numeroMesa      = jPrestamo.getInt("numeroMesa");
                Usuario u           = cafe.buscarUsuarioPorLogin(loginCliente);
                Mesa mesa           = buscarMesa(cafe, numeroMesa);
                if (u instanceof Cliente && mesa != null) {
                    prestamo = new PrestamoCliente(inicio, (Cliente) u, mesa);
                }

            } else if ("PrestamoEmpleado".equals(tipo)) {
                String loginEmpleado = jPrestamo.getString("loginEmpleado");
                Usuario u            = cafe.buscarUsuarioPorLogin(loginEmpleado);
                if (u instanceof Empleado) {
                    prestamo = new PrestamoEmpleado(inicio, (Empleado) u);
                }
            }

            if (prestamo == null) continue;

            // Fecha fin (null si el prestamo sigue activo)
            if (!jPrestamo.isNull("fechaFin")) {
                prestamo.setFechaFin(LocalDateTime.parse(jPrestamo.getString("fechaFin")));
            }

            // Juegos referenciados por nombre
            JSONArray jJuegos = jPrestamo.getJSONArray("juegos");
            for (int j = 0; j < jJuegos.length(); j++) {
                JuegoDeMesa juego = cafe.getInventarioPrestamo()
                                        .buscarPorNombre(jJuegos.getString(j));
                if (juego != null) {
                    prestamo.getJuegos().add(juego);
                }
            }
            cafe.registrarPrestamo(prestamo);
        }
    }

    private Mesa buscarMesa(Cafe cafe, int numero) {
        for (Mesa m : cafe.getMesas()) {
            if (m.getNumero() == numero) return m;
        }
        return null;
    }

    public void salvarPrestamos(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jPrestamos = new JSONArray();
        for (Prestamo prestamo : cafe.getHistorialPrestamos()) {
            JSONObject jPrestamo = new JSONObject();
            jPrestamo.put("tipo",        prestamo.getClass().getSimpleName());
            jPrestamo.put("fechaInicio", prestamo.getFechaInicio().toString());
            jPrestamo.put("fechaFin",    prestamo.getFechaFin() != null
                    ? prestamo.getFechaFin().toString()
                    : JSONObject.NULL);

            if (prestamo instanceof PrestamoCliente) {
                PrestamoCliente pc = (PrestamoCliente) prestamo;
                jPrestamo.put("loginCliente", pc.getCliente().getLogin());
                jPrestamo.put("numeroMesa",   pc.getMesa().getNumero());

            } else if (prestamo instanceof PrestamoEmpleado) {
                PrestamoEmpleado pe = (PrestamoEmpleado) prestamo;
                jPrestamo.put("loginEmpleado", pe.getEmpleado().getLogin());
            }

            // Solo guardamos el nombre de cada juego
            JSONArray jJuegos = new JSONArray();
            for (JuegoDeMesa juego : prestamo.getJuegos()) {
                jJuegos.put(juego.getNombre());
            }
            jPrestamo.put("juegos", jJuegos);
            jPrestamos.put(jPrestamo);
        }
        jobject.put("historialPrestamos", jPrestamos);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}