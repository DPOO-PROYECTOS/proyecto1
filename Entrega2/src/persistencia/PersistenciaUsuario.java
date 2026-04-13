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
import modelo.Usuario;

public class PersistenciaUsuario {

    /**
     * Carga los usuarios registrados desde el archivo JSON
     * @throws JSONException 
     */
    public void cargarUsuarios(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return; // Si no existe el archivo, no cargamos nada

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        // Validamos que exista la llave "usuarios"
        if (raiz.has("usuarios")) {
            JSONArray jUsuarios = raiz.getJSONArray("usuarios");
            for (int i = 0; i < jUsuarios.length(); i++) {
                JSONObject jUsuario = jUsuarios.getJSONObject(i);
                
                String login = jUsuario.getString("login");
                String password = jUsuario.getString("password");
                
                // Creamos el usuario y lo añadimos al modelo
                Usuario usuario = new Usuario(login, password);
                cafe.agregarUsuario(usuario);
            }
        }
    }

    /**
     * Guarda la lista de usuarios en el archivo JSON
     * @throws JSONException 
     */
    public void salvarUsuarios(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        JSONArray jUsuarios = new JSONArray();
        
        for (Usuario usuario : cafe.getUsuarios()) {
            JSONObject jUsuario = new JSONObject();
            jUsuario.put("login", usuario.getLogin());
            jUsuario.put("password", usuario.getPassword());
            jUsuarios.put(jUsuario);
        }
        
        jobject.put("usuarios", jUsuarios);
        
        // Escritura física en la carpeta /data
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}