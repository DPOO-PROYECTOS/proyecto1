package persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modelo.Cafe;
import modelo.JuegoDeMesa;
import modelo.Torneo;
import modelo.TorneoAmistoso; 
import modelo.TorneoCompetitivo; 
import modelo.Usuario;

public class PersistenciaTorneo {

    public void cargarTorneos(Cafe cafe, String archivo) throws IOException, JSONException {
        File f = new File(archivo);
        if (!f.exists()) return; 

        String jsonCompleto = new String(Files.readAllBytes(f.toPath()));
        JSONObject raiz = new JSONObject(jsonCompleto);

        if (raiz.has("torneos")) {
            JSONArray jTorneos = raiz.getJSONArray("torneos");
            for (int i = 0; i < jTorneos.length(); i++) {
                JSONObject jTorneo = jTorneos.getJSONObject(i);

                String diaSemana = jTorneo.getString("diaSemana");
                int maxParticipantes = jTorneo.getInt("maxParticipantes");
                String nombreJuego = jTorneo.getString("juego");
                String tipo = jTorneo.getString("tipo");

                // Buscar el juego original en el inventario del café
                JuegoDeMesa juegoEncontrado = null;
                for (JuegoDeMesa j : cafe.getInventarioVenta().getJuegos()) {
                    if (j.getNombre().equalsIgnoreCase(nombreJuego)) {
                        juegoEncontrado = j;
                        break;
                    }
                }

                if (juegoEncontrado == null) {
                    for (JuegoDeMesa j: cafe.getInventarioPrestamo().getJuegos()) {
                    	if(j.getNombre().equalsIgnoreCase(nombreJuego)) {
                    		juegoEncontrado= j;
                    		break;
                    	}
                    }
                	Torneo torneo = null;
                    
                    // torneo según su tipo
                    if (tipo.equals("TorneoAmistoso")) {
                        torneo = new TorneoAmistoso(juegoEncontrado, diaSemana, maxParticipantes);
                    } else if (tipo.equals("TorneoCompetitivo")) {
                        double tarifa = jTorneo.has("tarifa") ? jTorneo.getDouble("tarifa") : 0.0;
                        torneo = new TorneoCompetitivo(juegoEncontrado, diaSemana, maxParticipantes, tarifa);
                    }

                    if (torneo != null) {
                        //contadores de cupos
                        torneo.setCuposDisponibles(jTorneo.getInt("cuposDisponibles"));
                        torneo.setCuposFanaticosRestantes(jTorneo.getInt("cuposFanaticosRestantes"));

                        // mapa de inscripciones
                        if (jTorneo.has("inscripciones")) {
                            JSONArray jInscritos = jTorneo.getJSONArray("inscripciones");
                            for (int k = 0; k < jInscritos.length(); k++) {
                                JSONObject jInscripcion = jInscritos.getJSONObject(k);
                                String login = jInscripcion.getString("login");
                                int cupos = jInscripcion.getInt("cupos");

                                
                                for (Usuario u : cafe.getUsuarios()) {
                                    if (u.getLogin().equals(login)) {
                                        torneo.getInscripciones().put(u, cupos);
                                        break;
                                    }
                                }
                            }
                        }
                        
                        cafe.getTorneos().add(torneo);
                    }
                }
            }
        }
    }

    public void salvarTorneos(Cafe cafe, JSONObject jobject, String archivo) throws IOException, JSONException {
        JSONArray jTorneos = new JSONArray();
        
        for (Torneo torneo : cafe.getTorneos()) {
            JSONObject jTorneo = new JSONObject();
            jTorneo.put("diaSemana", torneo.getDiaSemana());
            jTorneo.put("maxParticipantes", torneo.getMaxParticipantes());
            jTorneo.put("juego", torneo.getJuego().getNombre()); 
            jTorneo.put("tipo", torneo.getClass().getSimpleName()); 
            jTorneo.put("cuposDisponibles", torneo.getCuposDisponibles());
            jTorneo.put("cuposFanaticosRestantes", torneo.getCuposFanaticosRestantes());

            
            if (torneo instanceof TorneoCompetitivo) {
                jTorneo.put("tarifa", ((TorneoCompetitivo) torneo).getTarifaEntrada());
            }

            
            JSONArray jInscripciones = new JSONArray();
            for (Map.Entry<Usuario, Integer> entry : torneo.getInscripciones().entrySet()) {
                JSONObject jInscripcion = new JSONObject();
                jInscripcion.put("login", entry.getKey().getLogin());
                jInscripcion.put("cupos", entry.getValue());
                jInscripciones.put(jInscripcion);
            }
            jTorneo.put("inscripciones", jInscripciones);
            
            jTorneos.put(jTorneo);
        }
        
        jobject.put("torneos", jTorneos);

        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        pw.println(jobject.toString(2));
        pw.close();
    }
}