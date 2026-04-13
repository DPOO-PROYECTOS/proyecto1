package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.Mesa;

public class PersistenciaMesa {

    public void cargarMesas(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jMesas = raiz.getJSONArray("mesas");
        for (int i = 0; i < jMesas.length(); i++) {
            JSONObject jMesa = jMesas.getJSONObject(i);
            int numero = jMesa.getInt("numero");
            int capacidad = jMesa.getInt("capacidad");
            int numPersonas = jMesa.getInt("numPersonas");
            boolean tieneNinos = jMesa.getBoolean("tieneNinos");
            boolean tieneJovenes = jMesa.getBoolean("tieneJovenes");

            Mesa mesa = new Mesa(numero, capacidad);
            mesa.setNumPersonas(numPersonas);
            mesa.setTieneNinos(tieneNinos);
            mesa.setTieneJovenes(tieneJovenes);
            cafe.agregarMesa(mesa);
        }
    }

    public void salvarMesas(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
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
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}