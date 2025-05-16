package src.it.unibs.ingsw.gestvisit;

//import it.unibs.mylib.*;
import it.unibs.mylib.InputDati;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class VisitManager {
    public Volontario volontarioCorrente; // Volontario corrente
    public Configuratore configuratoreCorrente; // Configuratore corrente
    public Utente utenteCorrente; // Utente corrente (Volontario o Configuratore)
    
    // Attributi------------------------------------------------------------------------------  
    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // Pool con thread caching
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);
    private final Utilita utilita = new Utilita(databaseUpdater);
    private final CredentialManager credentialManager = new CredentialManager(databaseUpdater);

    
    private static VisitManager instance;
    

    public static VisitManager getInstance() {
        if (instance == null) {
            instance = new VisitManager();
        }
        return instance;
    }

     //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {
        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
        // Avvia il thread di aggiornamento periodico
        databaseUpdater.avviaSincronizzazioneConSleep(); // Esegui ogni 5 secondi
        System.out.println("Thread di aggiornamento avviato.");
    }

    public void stopExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        databaseUpdater.arrestaSincronizzazioneConSleep();
        System.out.println("ExecutorService arrestato.");
        
    }


    //Autenticazione-------------------------------------------------------------------------
    /*public void autentica() {
        Utente utente = credentialManager.autentica();
        Menu menu = null; // Inizializza il menu a null

        if (utente instanceof Volontario) {
            setUtenteCorrente(volontarioCorrente);
            menu = new MenuVolontario();
        } else if (utente instanceof Configuratore) {
            utenteCorrente = configuratoreCorrente;
            menu = new MenuConfiguratore();
        } else {
            System.out.println("Autenticazione fallita.");
        }
        menu.mostraMenu(); // Mostra il menu corrispondente
    }*/
    public void autentica() {
    // Autentica l'utente tramite il credentialManager
    Utente utente = credentialManager.autentica();
    Menu menu = null; // Inizializza il menu come null

    if (utente instanceof Volontario) {
        setUtenteCorrente(utente); // Imposta l'utente corrente
        menu = new MenuVolontario(); // Mostra il menu per il volontario
    } else if (utente instanceof Configuratore) {
        setUtenteCorrente(utente); // Imposta l'utente corrente
        menu = new MenuConfiguratore(); // Mostra il menu per il configuratore
    } else {
        System.out.println("Autenticazione fallita.");
        return; // Esci dalla funzione
    }

    // Mostra il menu corrispondente
    if (menu != null) {
        menu.mostraMenu();
    }
}


    
    //Logiche per i luoghi-------------------------------------------------------------------------
    public void aggiungiLuogo() {
        utilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        utilita.stampaLuoghi();
    }

    //Logiche per i volontari-------------------------------------------------------------------------
    public void aggiungiVolontario() {
        utilita.aggiungiVolontario();
    }

    public void mostraVolontari() {
        utilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void mostraVisite() {        
        utilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        utilita.stampaVisitePerStato ();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        utilita.modificaMaxPersone(numeroMax);
        System.out.println("Numero massimo di persone per visita modificato a: " + numeroMax);
            
    }

    public void modificaDataVisita() {        
        utilita.modificaDataVisita();
    }

    

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        utilita.aggiungiVisita();
    }



    public void modificaStatoVisita() {
        utilita.modificaStatoVisita();
    }

    public void visualizzaArchivioStorico() {
        utilita.visualizzaArchivioStorico();
    } 

    public void visualizzaVisiteVolontario(){
        utilita.visualizzaVisiteVolontario(instance);
    }

    public Utente getTipoUtente(){
        return utenteCorrente;
    }
    
    public void setUtenteCorrente(Utente utente) {
    this.utenteCorrente = utente;
}
}
