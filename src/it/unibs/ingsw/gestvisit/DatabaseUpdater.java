package src.it.unibs.ingsw.gestvisit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class DatabaseUpdater {
    // HashMap per memorizzare i dati sincronizzati
    private ConcurrentHashMap<String, Volontario> volontariMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Configuratore> configuratoriMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Luogo> luoghiMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Visite> visiteMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private Thread aggiornamentoThread;
    private volatile boolean eseguiAggiornamento = true; // Variabile per controllare il ciclo

    public DatabaseUpdater(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void sincronizzaDalDatabase() {
        executorService.submit(() -> {
            try {
                // Logica per sincronizzare i dati dal database
                // Esegui qui il caricamento delle tabelle (volontari, configuratori, ecc.)
                caricaVolontari();
                caricaConfiguratori();
                caricaLuoghi();
                caricaVisite();
            } catch (Exception e) {
                System.out.println("Errore durante la sincronizzazione dal database: " + e.getMessage());
            }
        });
    }

    public void avviaSincronizzazioneConSleep() {
        eseguiAggiornamento = true; // Assicura che il ciclo sia attivo
        aggiornamentoThread = new Thread(() -> {
            while (eseguiAggiornamento) {
                try {
                    sincronizzaDalDatabase();
                    Thread.sleep(5000); // Pausa di 5 secondi
                } catch (InterruptedException e) {
                    System.out.println("Thread di aggiornamento interrotto.");
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
                }
            }
        });
        aggiornamentoThread.start();
    }

    public void arrestaSincronizzazioneConSleep() {
        eseguiAggiornamento = false; // Ferma il ciclo
        if (aggiornamentoThread != null) {
            aggiornamentoThread.interrupt(); // Interrompe il thread se è in attesa
            try {
                aggiornamentoThread.join(); // Attende la terminazione del thread
            } catch (InterruptedException e) {
                System.out.println("Errore durante l'arresto del thread di aggiornamento.");
                Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
            }
        }
    }

//Logiche dei volontari--------------------------------------------------
    // Metodo per caricare i volontari dal database e memorizzarli nella HashMap
    private void caricaVolontari() {
        String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (volontariMap) {
                volontariMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    Volontario volontario = new Volontario(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password"),
                            rs.getString("tipi_di_visite")
                    );
                    volontariMap.put(email, volontario);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei volontari: " + e.getMessage());
        }
    }

    // Metodo per aggiungere un volontario al database
    public void aggiungiVolontario(Volontario volontario) {
        String sql = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite) VALUES (?, ?, ?, ?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, volontario.getNome());
                pstmt.setString(2, volontario.getCognome());
                pstmt.setString(3, volontario.getEmail());
                pstmt.setString(4, volontario.getPassword());
                pstmt.setString(5, volontario.getTipiDiVisite());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta del volontario: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare un volontario nel database
    public void aggiornaPswVolontario(String email, String nuovaPassword) {
        String sqlVolontari = "UPDATE volontari SET password_modificata = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET password_modificata = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "volontari"
                try (PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari)) {
                    pstmtVolontari.setString(1, nuovaPassword);
                    pstmtVolontari.setString(2, email);
                    int rowsUpdatedVolontari = pstmtVolontari.executeUpdate();
    
                    if (rowsUpdatedVolontari > 0) {
                        System.out.println("Password aggiornata con successo nella tabella 'volontari'.");
                    } else {
                        System.out.println("Errore: Nessun volontario trovato con l'email specificata.");
                    }
                }
    
                // Aggiorna la tabella "utenti_unificati"
                try (PreparedStatement pstmtUtenti = conn.prepareStatement(sqlUtentiUnificati)) {
                    pstmtUtenti.setString(1, nuovaPassword);
                    pstmtUtenti.setString(2, email);
                    int rowsUpdatedUtenti = pstmtUtenti.executeUpdate();
    
                    if (rowsUpdatedUtenti > 0) {
                        System.out.println("Password aggiornata con successo nella tabella 'utenti_unificati'.");
                    } else {
                        System.out.println("Errore: Nessun utente trovato con l'email specificata nella tabella 'utenti_unificati'.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento della password: " + e.getMessage());
            }
        });
    }

//Logiche dei configuratori--------------------------------------------------
    // Metodo per caricare i configuratori dal database e memorizzarli nella HashMap
    private void caricaConfiguratori() {
        String sql = "SELECT nome, cognome, email, password FROM configuratori";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (configuratoriMap) {
                configuratoriMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    Configuratore configuratore = new Configuratore(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password")
                    );
                    configuratoriMap.put(email, configuratore);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei configuratori: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un configuratore nel database
    public void aggiornaConfiguratore(String email, Configuratore configuratoreAggiornato) {
        String sql = "UPDATE configuratori SET nome = ?, cognome = ?, password = ? WHERE email = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, configuratoreAggiornato.getNome());
                pstmt.setString(2, configuratoreAggiornato.getCognome());
                pstmt.setString(3, configuratoreAggiornato.getPassword());
                pstmt.setString(4, email);
    
                int rowsUpdated = pstmt.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.out.println("Configuratore aggiornato con successo.");
                } else {
                    System.out.println("Errore: Nessun configuratore trovato con l'email specificata.");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento del configuratore: " + e.getMessage());
            }
        });
    }

//Logiche dei luoghi--------------------------------------------------
    // Metodo per caricare i luoghi dal database e memorizzarli nella HashMap
    private void caricaLuoghi() {
        String sql = "SELECT nome, descrizione FROM luoghi";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (luoghiMap) {
                luoghiMap.clear();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    Luogo luogo = new Luogo(
                            nome,
                            rs.getString("descrizione")
                    );
                    luoghiMap.put(nome, luogo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un luogo nel database
    public void aggiornaLuogo(String nome, Luogo luogoAggiornato) {
        String sql = "UPDATE luoghi SET descrizione = ? WHERE nome = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogoAggiornato.getDescrizione());
                pstmt.setString(2, nome);
    
                int rowsUpdated = pstmt.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.out.println("Luogo aggiornato con successo.");
                } else {
                    System.out.println("Errore: Nessun luogo trovato con il nome specificato.");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento del luogo: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere un luogo al database
    public void aggiungiLuogo(Luogo luogo) {
        String sql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogo.getNome());
                pstmt.setString(2, luogo.getDescrizione());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta del luogo: " + e.getMessage());
            }
        });
    }

//Logiche delle visite--------------------------------------------------
    // Metodo per caricare un luogo nel database e memorizzarlo nella HashMap
    private void caricaVisite() {
        String sql = "SELECT id, luogo, tipo_visita, volontario, data, stato, max_persone FROM visite";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            synchronized (visiteMap) {
                visiteMap.clear();
                while (rs.next()) {
                    int id = rs.getInt("id"); // ID della visita
                    String luogo = rs.getString("luogo");
                    String tipoVisita = rs.getString("tipo_visita");
                    String volontario = rs.getString("volontario");
                    LocalDate data = rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null; // Converte la data in LocalDate
                    int maxPersone = rs.getInt("max_persone");
                    String stato = rs.getString("stato");

                    // Usa il costruttore completo di Visite
                    Visite visita = new Visite(id, luogo, tipoVisita, volontario, data, maxPersone, stato);
                    visiteMap.put(id, visita);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle visite: " + e.getMessage());
        }
    }


    // Metodo per aggiungere una visita al database
    public void aggiungiVisita(Visite visita) {
        String sql = "INSERT INTO visite (luogo, tipo_visita, volontario, data, stato, max_persone) VALUES (?, ?, ?, ?, ?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, visita.getLuogo());
                pstmt.setString(2, visita.getTipoVisita());
                pstmt.setString(3, visita.getVolontario());
                pstmt.setDate(4, visita.getData() != null ? java.sql.Date.valueOf(visita.getData()) : null);
                pstmt.setString(5, "Proposta"); // Stato iniziale
                pstmt.setInt(6, 10); // Valore predefinito per max_persone
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta della visita: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare una visita specifica
    public void aggiornaVisita(int visitaId, Visite visitaAggiornata) {
        String sql = "UPDATE visite SET luogo = ?, tipo_visita = ?, volontario = ?, data = ?, stato = ?, max_persone = ? WHERE id = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, visitaAggiornata.getLuogo());
                pstmt.setString(2, visitaAggiornata.getTipoVisita());
                pstmt.setString(3, visitaAggiornata.getVolontario());
                pstmt.setDate(4, visitaAggiornata.getData() != null ? java.sql.Date.valueOf(visitaAggiornata.getData()) : null);
                pstmt.setString(5, visitaAggiornata.getStato());
                pstmt.setInt(6, visitaAggiornata.getMaxPersone());
                pstmt.setInt(7, visitaId);

                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Visita aggiornata con successo.");
                } else {
                    System.out.println("Errore: Nessuna visita trovata con l'ID specificato.");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento della visita: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare il numero massimo di persone per tutte le visite
    public void aggiornaMaxPersonePerVisita(int maxPersonePerVisita) {
        String sql = "UPDATE visite SET max_persone = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maxPersonePerVisita);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Numero massimo di persone per visita aggiornato con successo.");
                } else {
                    System.out.println("Nessuna visita trovata da aggiornare.");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento del numero massimo di persone per visita: " + e.getMessage());
            }
        });
    }

//Getters e Setters--------------------------------------------------
    // Metodo per recuperare il numero massimo di persone per visita dal database
    public int getMaxPersoneDefault() {
        String sql = "SELECT valore FROM configurazioni WHERE chiave = 'max_persone_default'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                return rs.getInt("valore");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero del numero massimo di persone: " + e.getMessage());
        }
        return 10; // Valore di default se non trovato nel database
    }

    public ConcurrentHashMap<String, Volontario> getVolontariMap() {
        return volontariMap;
    }

    public ConcurrentHashMap<String, Configuratore> getConfiguratoriMap() {
        return configuratoriMap;
    }

    public ConcurrentHashMap<String, Luogo> getLuoghiMap() {
        return luoghiMap;
    }

    public ConcurrentHashMap<Integer, Visite> getVisiteMap() {
        return visiteMap;
    }
}