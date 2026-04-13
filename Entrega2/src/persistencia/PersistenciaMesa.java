package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.Mesa;

public class PersistenciaMesa {

    /**
     * Carga la configuración de las mesas desde un archivo JSON
     * @throws JSONException 
     */
    public void cargarMesas(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return; // Si el archivo no existe (primera corrida), no hacemos nada

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        // Verificamos que la llave "mesas" exista para evitar errores
        if (raiz.has("mesas")) {
            JSONArray jMesas = raiz.getJSONArray("mesas");
            for (int i = 0; i < jMesas.length(); i++) {
                JSONObject jMesa = jMesas.getJSONObject(i);
                
                int numero = jMesa.getInt("numero");
                int capacidad = jMesa.getInt("capacidad");
                int numPersonas = jMesa.getInt("numPersonas");
                boolean tieneNinos = jMesa.getBoolean("tieneNinos");
                boolean tieneJovenes = jMesa.getBoolean("tieneJovenes");

                // Creamos la mesa y seteamos su estado actual
                Mesa mesa = new Mesa(numero, capacidad);
                mesa.setNumPersonas(numPersonas);
                mesa.setTieneNinos(tieneNinos);
                mesa.setTieneJovenes(tieneJovenes);
                
                cafe.agregarMesa(mesa);
            }
        }
    }

    /**
     * Guarda el estado de las mesas en un archivo JSON
     * @throws JSONException 
     */
    public void salvarMesas(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        JSONArray jMesas = new JSONArray();
        
        for (Mesa mesa : cafe.getMesas()) {
            JSONObject jMesa = new JSONObject();
            jMesa.put("numero", mesa.getNumero());
            jMesa.put("capacidad", mesa.getCapacidad());
            jMesa.put("numPersonas", mesa.getNumPersonas());
            jMesa.put("tieneNinos", mesa.isTieneNinos());
            jMesa.put("tieneJovenes", mesa.isTieneJovenes());
            jMesas.put(jMesa);
        }
        
        jobject.put("mesas", jMesas);

        // Escritura del archivo en la carpeta /data
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}