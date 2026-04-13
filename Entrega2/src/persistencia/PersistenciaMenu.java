package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modelo.Bebida;
import modelo.Cafe;
import modelo.ItemMenu;
import modelo.Pasteleria;

public class PersistenciaMenu {

    /**
     * Carga el menú desde un archivo JSON y lo añade al objeto Cafe
     * @throws JSONException 
     */
    public void cargarMenu(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return;

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        if (raiz.has("menu")) {
            JSONArray jMenu = raiz.getJSONArray("menu");
            for (int i = 0; i < jMenu.length(); i++) {
                JSONObject jItem = jMenu.getJSONObject(i);
                
                String nombre = jItem.getString("nombre");
                double precio = jItem.getDouble("precio");
                String tipo = jItem.getString("tipo");

                if ("Bebida".equals(tipo)) {
                    boolean esAlcoholica = jItem.getBoolean("esAlcoholica");
                    boolean esCaliente   = jItem.getBoolean("esCaliente");
                    cafe.agregarItemMenu(new Bebida(nombre, precio, esAlcoholica, esCaliente));

                } else if ("Pasteleria".equals(tipo)) {
                    Pasteleria p = new Pasteleria(nombre, precio);
                    JSONArray jAlergenos = jItem.getJSONArray("alergenos");
                    for (int j = 0; j < jAlergenos.length(); j++) {
                        p.agregarAlergeno(jAlergenos.getString(j));
                    }
                    cafe.agregarItemMenu(p);
                }
            }
        }
    }

    public void salvarMenu(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        JSONArray jMenu = new JSONArray();
        
        for (ItemMenu item : cafe.getMenu()) {
            JSONObject jItem = new JSONObject();
            jItem.put("nombre", item.getNombre());
            jItem.put("precio", item.getPrecio());
            
            jItem.put("tipo", item.getClass().getSimpleName());

            if (item instanceof Bebida) {
                Bebida b = (Bebida) item;
                jItem.put("esAlcoholica", b.isEsAlcoholica());
                jItem.put("esCaliente",   b.isEsCaliente());

            } else if (item instanceof Pasteleria) {
                Pasteleria p = (Pasteleria) item;
                JSONArray jAlergenos = new JSONArray();
                for (String alergeno : p.getAlergenos()) {
                    jAlergenos.put(alergeno);
                }
                jItem.put("alergenos", jAlergenos);
            }
            jMenu.put(jItem);
        }
        
        jobject.put("menu", jMenu);

        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2)); 
        pw.close();
    }
}