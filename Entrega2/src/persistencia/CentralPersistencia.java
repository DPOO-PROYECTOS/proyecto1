package persistencia;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import modelo.Cafe;

public class CentralPersistencia {

    /**
     * Carga toda la información del sistema desde los archivos JSON
     * @throws JSONException 
     */
    public static void cargarTodo(Cafe cafe, String archivoUsuarios, String archivoInventario, String archivoMenu, String archivoMesas) throws IOException, JSONException {

        PersistenciaUsuario pU = new PersistenciaUsuario();
        pU.cargarUsuarios(cafe, archivoUsuarios);

        PersistenciaInventario pI = new PersistenciaInventario();
        pI.cargarInventario(cafe, archivoInventario);

        PersistenciaMenu pM = new PersistenciaMenu();
        pM.cargarMenu(cafe, archivoMenu);

        PersistenciaMesa pMe = new PersistenciaMesa();
        pMe.cargarMesas(cafe, archivoMesas);
    }

    /**
     * Guarda toda la información del sistema en archivos JSON
     * @throws JSONException 
     */
    public static void guardarTodo(Cafe cafe, String archivoUsuarios, String archivoInventario, String archivoMenu, String archivoMesas) throws IOException, JSONException {

        PersistenciaUsuario pU = new PersistenciaUsuario();
        PersistenciaInventario pI = new PersistenciaInventario();
        PersistenciaMenu pM = new PersistenciaMenu();
        PersistenciaMesa pMe = new PersistenciaMesa();

        // Se pasa un JSONObject nuevo a cada uno para que gestionen su propia raíz
        pU.salvarUsuarios(cafe, new JSONObject(), archivoUsuarios);
        pI.salvarInventario(cafe, new JSONObject(), archivoInventario);
        pM.salvarMenu(cafe, new JSONObject(), archivoMenu);
        pMe.salvarMesas(cafe, new JSONObject(), archivoMesas);
    }
}