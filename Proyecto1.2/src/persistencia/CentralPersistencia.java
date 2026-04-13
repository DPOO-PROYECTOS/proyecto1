package persistencia;



public class CentralPersistencia
{
	public static  getPersistenciaUsuario(Cafe cafe, String archivo) {
		cargarUsuarios(cafe, archivo);
	}
	
	public static  getPersistenciaInventario(Cafe cafe, String archivo) {
		cargarInventario(cafe, archivo);
	}
	
	public static  getPersistenciaMenu(Cafe cafe, String archivo) {
		cargarMenu(cafe, archivo);
	}
	public static  getPersistenciaMesa(Cafe cafe, String archivo) {
		cargarMesa(cafe, archivo);
}