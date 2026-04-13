package persistencia;

import java.io.IOException;

import modelo.Cafe;

public class CentralPersistencia {

    public static void cargarTodo(Cafe cafe, String archivoUsuarios, String archivoInventario,String archivoMenu, String archivoMesas) throws IOException {

        PersistenciaUsuario pU = new PersistenciaUsuario();
        pU.cargarUsuarios(cafe, archivoUsuarios);

        PersistenciaInventario pI = new PersistenciaInventario();
        pI.cargarInventario(cafe, archivoInventario);

        PersistenciaMenu pM = new PersistenciaMenu();
        pM.cargarMenu(cafe, archivoMenu);

        PersistenciaMesa pMe = new PersistenciaMesa();
        pMe.cargarMesas(cafe, archivoMesas);
    }
}