package src.it.unibs.ingsw.gestvisit;

import it.unibs.fp.mylib.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VisitManager {

    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private ArrayList<Volontario> volontari = new ArrayList<>();
    private ArrayList<Visite> tipiVisita = new ArrayList<>();    
    private ArrayList<Configuratore> configuratori = new ArrayList<>();
    private ArrayList<TemporaryCredential> temporaryCredentials = new ArrayList<>();
    private CredentialManager credentialManager = new CredentialManager();
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Pool con thread caching
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {
        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
    }

    public void stopExecutorService() {
        executorService.shutdown();
        System.out.println("ExecutorService arrestato.");
    }


    //Autenticazione-------------------------------------------------------------------------
    public void autentica() {
        String nomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nome utente (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        String ruolo = credentialManager.verificaCredenziali(nomeUtente, password);
        Boolean credenzialiModificate = credentialManager.isPasswordModificata(nomeUtente);
    
        if (ruolo == null) {
            System.out.println("Credenziali non valide.");
        } else {
            Menu menu;
            if (ruolo.equals("Volontario")) {
                System.out.println("Accesso come Volontario.");

                // Cerca il volontario corrispondente all'email
                Volontario volontario = databaseUpdater.getVolontariMap().get(nomeUtente);
                if (volontario == null) {
                    System.out.println("Errore: volontario non trovato.");
                    return;
                }
    
                // Controlla se il volontario ha credenziali temporanee
                if (credenzialiModificate) {
                    System.out.println("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    modificaCredenzialiVolontario(volontario);
                }
    
                menu = new MenuVolontario();
            } else if (ruolo.equals("Configuratore")) {
                System.out.println("Accesso come Configuratore.");
                menu = new MenuConfiguratore();
            } else if(ruolo.equals("TEMP")){
                System.out.println("Accesso come utente temporaneo.");
                modificaCredenzialiConfiguratore();
                menu = new MenuConfiguratore();
            } else {
                System.out.println("Ruolo non riconosciuto: " + ruolo);
                return;
            }
            menu.mostraMenu();
        }
    }

    
    //Logiche per i luoghi-------------------------------------------------------------------------
    public void aggiungiLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");
        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        databaseUpdater.getLuoghiMap().put(nome, nuovoLuogo);
        System.out.println("Luogo aggiunto: " + nuovoLuogo);
    }

    public void showLuoghi() {
        Utilita.stampaLuoghi();
    }

    //Logiche per i volontari-------------------------------------------------------------------------
    public void addVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        Volontario volontario = new Volontario(nome, cognome, email, nomeUtente, password);
        volontari.add(volontario);

        Utilita.salvaVolontari(volontario);
    }

    public void showVolontari() {
        Utilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void assegnaVisita() {
        Utilita.assegnaVisita();
    }

    public void showVisite() {
        Utilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        Utilita.visualizzaVisitePerStato ();
    }

    public void modifycaNumeroMaxPersonePerVisita() {
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        Utilita.setMaxPersonePerVisita(numeroMax);
        System.out.println("Numero massimo di persone per visita modificato a: " + numeroMax);
            
    }

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        String luogo = InputDati.leggiStringaNonVuota("Inserisci il luogo della visita: ");
        String tipoVisita = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");
        String volontario = InputDati.leggiStringaNonVuota("Inserisci il nome del volontario: ");
    
        // Chiedi all'utente se vuole inserire una data
        boolean risposta = InputDati.yesOrNo("Vuoi inserire una data per la visita? (sì/no): ");
        LocalDate data = null;
    
        if (risposta) {
            int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
            int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
            int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
            data = LocalDate.of(anno, mese, giorno);
        }
    
        // Crea l'oggetto Visite
        Visite nuovaVisita = new Visite(luogo, tipoVisita, volontario, data);
    
        // Genera un ID univoco per la visita
        int id = databaseUpdater.getVisiteMap().size() + 1;
        databaseUpdater.getVisiteMap().put(id, nuovaVisita);
    
        System.out.println("Visita aggiunta: " + nuovaVisita);
    }

    public void modificaStatoVisita() {
        Utilita.modificaStatoVisita();
    }

    public void visualizzaArchivioStorico() {
        Utilita.visualizzaArchivioStorico();
    }

    //Logiche per le credenziali-------------------------------------------------------------------------
    public void modificaCredenzialiConfiguratore() {
        credentialManager.saveNewConfigCredential(configuratori);
    }

    public void modificaCredenzialiVolontario(Volontario volontario) {
        credentialManager.saveNewVolCredential(volontario);
    }

    public void leggiCredenziali(){
        credentialManager.caricaCredenzialiVolontario(volontari);
        credentialManager.caricaCredenzialiTemporanee(temporaryCredentials);
        credentialManager.caricaCredenzialiConfiguratore(configuratori);
    }
}
