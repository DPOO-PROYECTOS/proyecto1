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
import modelo.Empleado;
import modelo.Turno;
import modelo.SolicitudCambioTurno;
import modelo.Usuario;

public class PersistenciaTurnos {

    public void cargarTurnos(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return;

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        // CARGAR TURNOS
        if (raiz.has("turnos")) {
            JSONArray jTurnos = raiz.getJSONArray("turnos");
            for (int i = 0; i < jTurnos.length(); i++) {
                JSONObject jTurno = jTurnos.getJSONObject(i);
                String dia = jTurno.getString("dia");
                String horaInicio = jTurno.getString("horaInicio");
                String horaFin = jTurno.getString("horaFin");
                String loginEmpleado = jTurno.getString("empleado");

                Usuario usu = cafe.buscarUsuarioPorLogin(loginEmpleado);
                if (usu instanceof Empleado) {
                    Turno turno = new Turno(dia, horaInicio, horaFin, (Empleado) usu);
                    cafe.getPlanSemanal().agregarTurno(turno); 
                }
            }
        }

        // 
        if (raiz.has("solicitudes")) {
            JSONArray jSol = raiz.getJSONArray("solicitudes");
            for (int i = 0; i < jSol.length(); i++) {
                JSONObject jS = jSol.getJSONObject(i);
                String loginSolicitante = jS.getString("solicitante");
                String loginCompa = jS.has("intercambiarCon") ? jS.getString("intercambiarCon") : "";
                String diaTurno = jS.getString("diaTurno");


                Empleado solicitante = (Empleado) cafe.buscarUsuarioPorLogin(loginSolicitante);
                Empleado compa = loginCompa.isEmpty() ? null : (Empleado) cafe.buscarUsuarioPorLogin(loginCompa);
                Turno turnoOriginal = null;
                for (Turno t : cafe.getPlanSemanal().getTurnosDia(diaTurno)) {
                    if (t.getEmpleado().equals(solicitante)) {
                        turnoOriginal = t;
                        break;
                    }
                }

                if (turnoOriginal != null && solicitante != null) {
                    SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turnoOriginal, solicitante, compa);
                    cafe.agregarSolicitudCambioTurno(solicitud);
                }
            }
        }
    }

    public void salvarTurnos(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        // GUARDAR TURNOS
        JSONArray jTurnos = new JSONArray();
        if (cafe.getPlanSemanal() != null && cafe.getPlanSemanal().getTurnos() != null) {
            for (Turno t : cafe.getPlanSemanal().getTurnos()) {
                JSONObject jTurno = new JSONObject();
                jTurno.put("dia", t.getDia());
                jTurno.put("horaInicio", t.getHoraInicio());
                jTurno.put("horaFin", t.getHoraFin());
                jTurno.put("empleado", t.getEmpleado().getLogin());
                jTurnos.put(jTurno);
            }
        }
        jobject.put("turnos", jTurnos);

        
        JSONArray jSolicitudes = new JSONArray();
        for (SolicitudCambioTurno sol : cafe.getSolicitudesCambioTurno()) {
            JSONObject jSol = new JSONObject();
            jSol.put("solicitante", sol.getSolicitante().getLogin());
            if (sol.getIntercambiarCon() != null) {
                jSol.put("intercambiarCon", sol.getIntercambiarCon().getLogin());
            }
            jSol.put("diaTurno", sol.getTurno().getDia());
            
            jSolicitudes.put(jSol);
        }
        jobject.put("solicitudes", jSolicitudes);

        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}