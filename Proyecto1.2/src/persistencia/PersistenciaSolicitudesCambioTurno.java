package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Admin;
import modelo.Cafe;
import modelo.Empleado;
import modelo.SolicitudCambioTurno;
import modelo.Turno;
import modelo.Usuario;

public class PersistenciaSolicitudesCambioTurno {

    public void cargarSolicitudes(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jSolicitudes = raiz.getJSONArray("solicitudesCambioTurno");
        for (int i = 0; i < jSolicitudes.length(); i++) {
            JSONObject jSol = jSolicitudes.getJSONObject(i);

            Empleado solicitante  = buscarEmpleado(cafe, jSol.getString("loginSolicitante"));
            Empleado intercambiar = buscarEmpleado(cafe, jSol.getString("loginIntercambiarCon"));
            Turno turno           = buscarTurno(cafe, jSol.getString("loginSolicitante"),
                                                      jSol.getString("diaTurno"),
                                                      jSol.getString("horaInicioTurno"));

            if (solicitante == null || intercambiar == null || turno == null) continue;

            SolicitudCambioTurno sol = new SolicitudCambioTurno(turno, solicitante, intercambiar);
            sol.setEstado(jSol.getString("estado"));
            sol.setAprobada("aprobada".equals(jSol.getString("estado")));

            if (!jSol.isNull("loginAdmin")) {
                Usuario u = cafe.buscarUsuarioPorLogin(jSol.getString("loginAdmin"));
                if (u instanceof Admin) {
                    sol.setAprobadaPor((Admin) u);
                }
            }
            cafe.agregarSolicitudCambioTurno(sol);
        }
    }

    private Empleado buscarEmpleado(Cafe cafe, String login) {
        Usuario u = cafe.buscarUsuarioPorLogin(login);
        return (u instanceof Empleado) ? (Empleado) u : null;
    }

    // Identifica el turno por login del empleado + dia + horaInicio
    private Turno buscarTurno(Cafe cafe, String loginEmpleado, String dia, String horaInicio) {
        for (Turno t : cafe.getPlanSemanal().getTurnos()) {
            if (t.getEmpleado().getLogin().equalsIgnoreCase(loginEmpleado)
                    && t.getDia().equalsIgnoreCase(dia)
                    && t.getHoraInicio().equals(horaInicio)) {
                return t;
            }
        }
        return null;
    }

    public void salvarSolicitudes(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jSolicitudes = new JSONArray();
        for (SolicitudCambioTurno sol : cafe.getSolicitudesCambioTurno()) {
            JSONObject jSol = new JSONObject();
            jSol.put("loginSolicitante",    sol.getSolicitante().getLogin());
            jSol.put("loginIntercambiarCon", sol.getIntercambiarCon().getLogin());
            jSol.put("diaTurno",            sol.getTurno().getDia());
            jSol.put("horaInicioTurno",     sol.getTurno().getHoraInicio());
            jSol.put("estado",              sol.getEstado());
            jSol.put("loginAdmin",          sol.getAprobadaPor() != null
                    ? sol.getAprobadaPor().getLogin()
                    : JSONObject.NULL);
            jSolicitudes.put(jSol);
        }
        jobject.put("solicitudesCambioTurno", jSolicitudes);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}