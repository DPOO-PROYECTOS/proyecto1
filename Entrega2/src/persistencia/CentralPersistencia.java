package persistencia;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import modelo.Cafe;

public class CentralPersistencia {

    public static void cargarTodo(Cafe cafe, String archivoUsuarios, String archivoInventarioPrestamo, String archivoMenu, String archivoMesas, String archivoInventarioVentas) throws IOException, JSONException {

        PersistenciaUsuario pU = new PersistenciaUsuario();
        pU.cargarUsuarios(cafe, archivoUsuarios);

        PersistenciaInventarioPrestamo pIP = new PersistenciaInventarioPrestamo();
        pIP.cargarInventarioPrestamo(cafe, archivoInventarioPrestamo);
        
        PersistenciaInventarioVenta pIV = new PersistenciaInventarioVenta();
        pIV.cargarInventarioVenta(cafe, archivoInventarioVentas);

        PersistenciaMenu pM = new PersistenciaMenu();
        pM.cargarMenu(cafe, archivoMenu);

        PersistenciaMesa pMe = new PersistenciaMesa();
        pMe.cargarMesas(cafe, archivoMesas);
    }

    public static void guardarTodo(Cafe cafe, String archivoUsuarios, String archivoInventarioPrestamo, String archivoMenu, String archivoMesas, String archivoInventarioVentas) throws IOException, JSONException {

        PersistenciaUsuario pU = new PersistenciaUsuario();
        PersistenciaInventarioPrestamo pIP = new PersistenciaInventarioPrestamo();
        PersistenciaInventarioVenta pIV = new PersistenciaInventarioVenta();
        PersistenciaMenu pM = new PersistenciaMenu();
        PersistenciaMesa pMe = new PersistenciaMesa();

        pU.salvarUsuarios(cafe, new JSONObject(), archivoUsuarios);
        pIP.salvarInventarioPrestamo(cafe, new JSONObject(), archivoInventarioPrestamo);
        pIV.salvarInventarioVenta(cafe, new JSONObject(), archivoInventarioVentas);
        pM.salvarMenu(cafe, new JSONObject(), archivoMenu);
        pMe.salvarMesas(cafe, new JSONObject(), archivoMesas);
    }
}