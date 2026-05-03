package modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    // Attributes
    private String login;
    private String password;
    private List<JuegoDeMesa> favoritos = new ArrayList<>();
    private boolean tieneBonoTorneoAmistoso;



    // Constructor
    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
    }



    // Methods
    public boolean getTieneBonoTorneoAmistoso() {return this.tieneBonoTorneoAmistoso;}
    public void setTieneBonoTorneoAmistoso(boolean t) {this.tieneBonoTorneoAmistoso=t;}
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<JuegoDeMesa> getFavoritos() { return favoritos; }
    public void setFavoritos(List<JuegoDeMesa> favoritos) { this.favoritos = favoritos; }


    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }


    public void agregarFavorito(JuegoDeMesa juego) {
        if (!favoritos.contains(juego)) {
            favoritos.add(juego);
        }
    }


    public void quitarFavorito(JuegoDeMesa juego) {
        favoritos.remove(juego);
    }


    public boolean esFavorito(JuegoDeMesa juego) {
        return favoritos.contains(juego);
    }
}
