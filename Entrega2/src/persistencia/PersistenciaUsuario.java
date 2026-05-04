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

    public void cargarUsuarios(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return; 

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        if (raiz.has("usuarios")) {
            JSONArray jUsuarios = raiz.getJSONArray("usuarios");
            for (int i = 0; i < jUsuarios.length(); i++) {
                JSONObject jUsuario = jUsuarios.getJSONObject(i);
                
                String login = jUsuario.getString("login");
                String password = jUsuario.getString("password");
                
                String tipo = jUsuario.has("tipo") ? jUsuario.getString("tipo") : "Usuario";
                
                Usuario usuario = null;
                
                
                if (tipo.equals("Admin")) {
                    usuario = new modelo.Admin(login, password);
                } else if (tipo.equals("Cliente")) {
                    modelo.Cliente cliente = new modelo.Cliente(login, password);
                    if (jUsuario.has("tieneBonoTorneoAmistoso")) {
                        cliente.setTieneBonoTorneoAmistoso(jUsuario.getBoolean("tieneBonoTorneoAmistoso"));
                    }
                    usuario = cliente;
                } else if (tipo.equals("Mesero")) {
                    usuario = new modelo.Mesero(login, password);
                } else if (tipo.equals("Empleado")) {
                    usuario = new modelo.Empleado(login, password, "cocinero");
                } else {
                    usuario = new modelo.Usuario(login, password);
                }
                cafe.agregarUsuario(usuario);
            }
        }
    }

    public void salvarUsuarios(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        JSONArray jUsuarios = new JSONArray();
        
        for (Usuario usuario : cafe.getUsuarios()) {
            JSONObject jUsuario = new JSONObject();
            jUsuario.put("login", usuario.getLogin());
            jUsuario.put("password", usuario.getPassword());
            jUsuario.put("tipo", usuario.getClass().getSimpleName());
            if (usuario instanceof modelo.Cliente) {
                jUsuario.put("tieneBonoTorneoAmistoso", usuario.getTieneBonoTorneoAmistoso());
            }
            jUsuarios.put(jUsuario);
        }
        
        jobject.put("usuarios", jUsuarios);
        
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}