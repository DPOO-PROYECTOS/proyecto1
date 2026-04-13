package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Admin;
import modelo.Cafe;
import modelo.Empleado;
import modelo.ItemMenu;
import modelo.SugerenciaPlatillo;
import modelo.Usuario;

public class PersistenciaSugerencias {

    public void cargarSugerencias(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jSugerencias = raiz.getJSONArray("sugerencias");
        for (int i = 0; i < jSugerencias.length(); i++) {
            JSONObject jSug = jSugerencias.getJSONObject(i);

            String descripcion    = jSug.getString("descripcion");
            boolean aprobada      = jSug.getBoolean("aprobada");
            Empleado empleado     = buscarEmpleado(cafe, jSug.getString("loginEmpleado"));
            ItemMenu item         = buscarItemMenu(cafe, jSug.getString("nombreItem"));

            if (empleado == null || item == null) continue;

            SugerenciaPlatillo sug = new SugerenciaPlatillo(descripcion, item, empleado);
            sug.setAprobada(aprobada);

            if (!jSug.isNull("loginAdmin")) {
                Usuario u = cafe.buscarUsuarioPorLogin(jSug.getString("loginAdmin"));
                if (u instanceof Admin) {
                    sug.setAdmin((Admin) u);
                }
            }
            cafe.agregarSugerencia(sug);
        }
    }

    private Empleado buscarEmpleado(Cafe cafe, String login) {
        Usuario u = cafe.buscarUsuarioPorLogin(login);
        return (u instanceof Empleado) ? (Empleado) u : null;
    }

    private ItemMenu buscarItemMenu(Cafe cafe, String nombre) {
        for (ItemMenu item : cafe.getMenu()) {
            if (item.getNombre().equalsIgnoreCase(nombre)) return item;
        }
        return null;
    }

    public void salvarSugerencias(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jSugerencias = new JSONArray();
        for (SugerenciaPlatillo sug : cafe.getSugerencias()) {
            JSONObject jSug = new JSONObject();
            jSug.put("descripcion",   sug.getDescripcion());
            jSug.put("aprobada",      sug.isAprobada());
            jSug.put("loginEmpleado", sug.getEmpleado().getLogin());
            jSug.put("nombreItem",    sug.getItemSugerido().getNombre());
            jSug.put("loginAdmin",    sug.getAdmin() != null
                    ? sug.getAdmin().getLogin()
                    : JSONObject.NULL);
            jSugerencias.put(jSug);
        }
        jobject.put("sugerencias", jSugerencias);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}