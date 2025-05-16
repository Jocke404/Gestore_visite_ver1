package src.it.unibs.ingsw.gestvisit;

import java.util.ArrayList;
import java.util.List;

public class Volontario implements Utente {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private List<String> tipiDiVisite = new ArrayList<>();

    // Costruttore, getter e setter
   public Volontario(String nome, String cognome, String email, String password, String tipiDiVisite) {
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.password = password;
    this.tipiDiVisite = new ArrayList<>();
    for (String tipo : tipiDiVisite.split(",")) {
        this.tipiDiVisite.add(tipo.trim());
    }
}
    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getEmail() {
        return email;
    }
    
    @Override
    public String getCognome() {
        return cognome;
    }
    
    public List<String> getTipiDiVisite() {
    return tipiDiVisite;
}

    public void setTipiDiVisite(List<String> tipiDiVisite) {
    this.tipiDiVisite = tipiDiVisite;
}
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Volontario [nome=" + nome + ", cognome=" + cognome + ", email=" + email + ", tipiDiVisite=" + tipiDiVisite + "]";
    }
}
