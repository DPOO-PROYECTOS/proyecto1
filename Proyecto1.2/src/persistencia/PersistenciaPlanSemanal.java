package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.Empleado;
import modelo.Turno;
import modelo.Usuario;

public class PersistenciaPlanSemanal {

    public void cargarPlanSemanal(Cafe cafe, String archivo) throws IOException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        JSONArray jTurnos = raiz.getJSONArray("planSemanal");
        for (int i = 0; i < jTurnos.length(); i++) {
            JSONObject jTurno = jTurnos.getJSONObject(i);
            String dia        = jTurno.getString("dia");
            String horaInicio = jTurno.getString("horaInicio");
            String horaFin    = jTurno.getString("horaFin");
            String login      = jTurno.getString("loginEmpleado");

            Usuario u = cafe.buscarUsuarioPorLogin(login);
            if (u instanceof Empleado) {
                Turno turno = new Turno(dia, horaInicio, horaFin, (Empleado) u);
                cafe.getPlanSemanal().agregarTurno(turno);
            }
        }
    }

    public void salvarPlanSemanal(Cafe cafe, JSONObject jobject, String archivo) throws IOException {
        JSONArray jTurnos = new JSONArray();
        for (Turno turno : cafe.getPlanSemanal().getTurnos()) {
            JSONObject jTurno = new JSONObject();
            jTurno.put("dia",           turno.getDia());
            jTurno.put("horaInicio",    turno.getHoraInicio());
            jTurno.put("horaFin",       turno.getHoraFin());
            jTurno.put("loginEmpleado", turno.getEmpleado().getLogin());
            jTurnos.put(jTurno);
        }
        jobject.put("planSemanal", jTurnos);
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}