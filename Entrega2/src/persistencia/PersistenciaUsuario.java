package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.Usuario;

public class PersistenciaUsuario {

    public void cargarUsuarios(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jUsuarios = raiz.getJSONArray("usuarios");
        for (int i = 0; i < jUsuarios.length(); i++) {
            JSONObject jUsuario = jUsuarios.getJSONObject(i);
            String login = jUsuario.getString("login");
            String password = jUsuario.getString("password");
            Usuario usuario = new Usuario(login, password);
            cafe.agregarUsuario(usuario);
        }
    }

    public void salvarUsuarios(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jUsuarios = new JSONArray();
        for (Usuario usuario : cafe.getUsuarios()) {
            JSONObject jUsuario = new JSONObject();
            jUsuario.put("login", usuario.getLogin());
            jUsuario.put("password", usuario.getPassword());
            jUsuarios.put(jUsuario);
        }
        jobject.put("usuarios", jUsuarios);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}