package src.it.unibs.ingsw.gestvisit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unibs.mylib.InputDati;

public class Utilita {

    private final DatabaseUpdater databaseUpdater;
   private final Map<String, List<String>> disponibilitaVolontari = new ConcurrentHashMap<>();


    public Utilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    // Metodo per stampare i luoghi
    public void stampaLuoghi() {
        ConcurrentHashMap<String, Luogo> luoghiMap = databaseUpdater.getLuoghiMap();

        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }

        System.out.println("Luoghi:");
        for (Luogo luogo : luoghiMap.values()) {
            System.out.println("Nome: " + luogo.getNome());
            System.out.println("Descrizione: " + luogo.getDescrizione());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare i volontari
    public void stampaVolontari() {
        ConcurrentHashMap<String, Volontario> volontariMap = databaseUpdater.getVolontariMap();

        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }

        System.out.println("Volontari:");
        for (Volontario volontario : volontariMap.values()) {
            System.out.println("Nome: " + volontario.getNome() + " " + volontario.getCognome());
            System.out.println("Email: " + volontario.getEmail());
            System.out.println("Tipi di Visite: " + volontario.getTipiDiVisite());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare le visite
    public void stampaVisite() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

        System.out.println("Visite:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.println("ID: " + entry.getKey());
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Volontario: " + visita.getVolontario());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("-------------------------");
        }
    }

    // Metodo per visualizzare le visite per stato
    public void stampaVisitePerStato() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili.");
            return;
        }

        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }

        int sceltaStato = InputDati.leggiIntero("Seleziona lo stato da visualizzare: ", 1, stati.length) - 1;
        String statoScelto = stati[sceltaStato];

        System.out.printf("Visite in stato '%s':%n", statoScelto);
        for (Visite visita : visiteMap.values()) {
            if (visita.getStato().equalsIgnoreCase(statoScelto)) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data");
            }
        }
    }

    // Metodo per visualizzare l'archivio storico delle visite
    public void visualizzaArchivioStorico() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili nell'archivio storico.");
            return;
        }

        System.out.println("Archivio storico delle visite effettuate:");
        for (Visite visita : visiteMap.values()) {
            if ("Effettuata".equalsIgnoreCase(visita.getStato())) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data");
            }
        }
    }


    // Metodo per visualizzare le visite assegnate a un volontario
   /*  public void visualizzaVisiteVolontario() {
        
        Utente utenteCorrente = visitManager.getTipoUtente();

       
    if (utenteCorrente == null) {
        System.out.println("Utente corrente è null.");
    } else {
        System.out.println("Tipo utente corrente: " + utenteCorrente.getClass().getSimpleName());
    }   
    
        if (!(utenteCorrente instanceof Volontario)) {
            System.out.println("Nessun volontario trovato o utente non autenticato.");
            return;
        }
    
        Volontario volontario = (Volontario) utenteCorrente;
        System.out.println("Visite assegnate a " + volontario.getNome() + " " + volontario.getCognome() + ":");
    
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
        boolean visiteTrovate = false;
    
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            if (visita.getVolontario().equals(volontario.getEmail())) {
                System.out.println("ID: " + entry.getKey());
                System.out.println("Luogo: " + visita.getLuogo());
                System.out.println("Tipo Visita: " + visita.getTipoVisita());
                System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
                System.out.println("Stato: " + visita.getStato());
                System.out.println("-------------------------");
                visiteTrovate = true;
            }
        }
    
        if (!visiteTrovate) {
            System.out.println("Nessuna visita assegnata al volontario.");
        }
    }*/
    public void visualizzaVisiteVolontario(VisitManager visitManager) {
    // Recupera l'utente corrente dall'istanza esistente di VisitManager
    Utente utenteCorrente = visitManager.getTipoUtente();

    // Debug: Verifica l'utente corrente
    if (utenteCorrente == null) {
        System.out.println("Utente corrente è null.");
        return;
    } else {
        System.out.println("Tipo utente corrente: " + utenteCorrente.getClass().getSimpleName());
    }

    // Verifica che l'utente sia un Volontario
    if (!(utenteCorrente instanceof Volontario)) {
        System.out.println("Nessun volontario trovato o utente non autenticato.");
        return;
    }

    // Cast a Volontario
    Volontario volontario = (Volontario) utenteCorrente;
    System.out.println("Visite assegnate a " + volontario.getNome() + " " + volontario.getCognome() + ":");

    // Recupera le visite
    ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    boolean visiteTrovate = false;

    for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
        Visite visita = entry.getValue();
        if (visita.getVolontario().equals(volontario.getNome() + " " + volontario.getCognome())) {
            System.out.println("ID: " + entry.getKey());
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("Stato: " + visita.getStato());
            System.out.println("-------------------------");
            visiteTrovate = true;
        }
    } 

    if (!visiteTrovate) {
        System.out.println("Nessuna visita assegnata al volontario.");
    }
}


    // Metodo per modificare la data di una visita
    public void modificaDataVisita() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }

        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                    visita.getData() != null ? visita.getData() : "Nessuna data");
        }

        int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("ID visita non valido.");
            return;
        }

        int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
        int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
        int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
        LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setData(nuovaData);

        databaseUpdater.aggiornaVisita(visitaId, visitaAggiornata);
        System.out.println("Data della visita aggiornata con successo.");
    }

    // Metodo per impostare il numero massimo di persone per visita
    public void modificaMaxPersone(int maxPersonePerVisita) {
        databaseUpdater.aggiornaMaxPersonePerVisita(maxPersonePerVisita);
        System.out.println("Numero massimo di persone per visita aggiornato a: " + maxPersonePerVisita);
    }

    // Metodo per visualizzare le visite in base allo stato
    public void modificaStatoVisita() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }
    
        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getStato());
        }
    
        int visitaId = InputDati.leggiIntero("Seleziona la visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("Visita non valida.");
            return;
        }
    
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
    
        int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        String nuovoStato = stati[sceltaStato];
    
        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setStato(nuovoStato);
    
        // Aggiorna la visita nel database
        databaseUpdater.aggiornaVisita(visitaId, visitaAggiornata);
        System.out.println("Stato della visita aggiornato con successo.");
    }

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        ConcurrentHashMap<String, Luogo> luoghiMap = databaseUpdater.getLuoghiMap();
        ConcurrentHashMap<String, Volontario> volontariMap = databaseUpdater.getVolontariMap();
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    
        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }
    
        System.out.println("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        for (int i = 0; i < luoghiNomi.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, luoghiNomi.get(i));
        }
    
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);
    
        String tipoVisitaScelto = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");
    
        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }
        
        System.out.println("\nElenco dei volontari disponibili:");
        List<Volontario> volontariNomi = new ArrayList<>(volontariMap.values());//TODO: controllare se è giusto
        for (int i = 0; i < volontariNomi.size(); i++) {
            System.out.printf("%d. %s %s, " + "tipo di visita: " + "%s%n", i + 1, volontariNomi.get(i).getNome(), volontariNomi.get(i).getCognome(), volontariNomi.get(i).getTipiDiVisite());
        }
        // Chiedi all'utente di selezionare un volontario
        int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontariNomi.size()) - 1;
        String volontarioNomeScelto = volontariNomi.get(volontarioIndex).getNome() + " " + volontariNomi.get(volontarioIndex).getCognome();
    
        LocalDate dataVisita;
        if (InputDati.yesOrNo("Vuoi inserire una data personale? ")) {
            int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
            int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
            int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
            dataVisita = LocalDate.of(anno, mese, giorno);
        } else {
            LocalDate oggi = LocalDate.now();
            YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
            List<LocalDate> dateValide = new ArrayList<>();
    
            for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
                LocalDate data = meseTarget.atDay(giorno);
                if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    dateValide.add(data);
                }
            }
    
            System.out.println("\nDate disponibili per la visita:");
            for (int i = 0; i < dateValide.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, dateValide.get(i));
            }
    
            int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            dataVisita = dateValide.get(dataIndex);
        }
    
        int maxPersone = databaseUpdater.getMaxPersoneDefault();
        String stato = "Proposta"; // Stato iniziale della visita
    
        // Genera un ID univoco per la visita
        int id = visiteMap.size() + 1;
    
        // Crea l'oggetto Visite utilizzando il costruttore completo
        Visite nuovaVisita = new Visite(id, luogoNomeScelto, tipoVisitaScelto, volontarioNomeScelto, dataVisita, maxPersone, stato);
        visiteMap.put(id, nuovaVisita);

        databaseUpdater.aggiungiNuovaVisita(nuovaVisita);
    
        System.out.println("Visita assegnata con successo per la data " + dataVisita + "!");
    }

    // Metodo per aggiungere un volontario
    public void aggiungiVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        
        Volontario nuovoVolontario = new Volontario(nome, cognome, email, nomeUtente, password);
        // Aggiungi il volontario alla HashMap
        databaseUpdater.getVolontariMap().putIfAbsent(email, nuovoVolontario);

        // Sincronizza con il database
        databaseUpdater.aggiungiNuovoVolontario(nuovoVolontario);
    }

    // Metodo per aggiungere un luogo
    public void aggiungiLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");

        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        databaseUpdater.getLuoghiMap().putIfAbsent(nome, nuovoLuogo);
        databaseUpdater.aggiungiNuovoLuogo(nuovoLuogo);  
        System.out.println("Luogo aggiunto: " + nuovoLuogo);
    }

    public void visualizzaVisiteVolontario (Volontario volontario){
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
        for(int i = 0; i < visiteMap.size(); i++){
            if(visiteMap.get(i).getVolontario().equals(volontario)){
                System.out.println(visiteMap.get(i));
            }
        }
    }

   public void inserisciDisponibilitaVolontario(Volontario volontario) {
    LocalDate oggi = LocalDate.now();
    int giornoCorrente = oggi.getDayOfMonth();

    // Permetti solo tra il 1 e il 15 del mese
    //if (giornoCorrente < 1 || giornoCorrente > 15) {
       // System.out.println("Puoi inserire la disponibilità solo tra il 1 e il 15 del mese precedente a quello di interesse.");
        //return;
    //}

    LocalDate meseProssimo = oggi.plusMonths(1);
    YearMonth ym = YearMonth.of(meseProssimo.getYear(), meseProssimo.getMonthValue());
    ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

    // Ottieni i tipi di visita associati al volontario
    List<String> tipiVisitaVolontario = volontario.getTipiDiVisite();

    System.out.println("Calendario del mese di " + meseProssimo.getMonth() + " " + meseProssimo.getYear() + ":");
System.out.println("Giorno\tGiorno della settimana");

List<Integer> giorniDisponibili = new ArrayList<>();
for (int giorno = 1; giorno <= ym.lengthOfMonth(); giorno++) {
    LocalDate data = ym.atDay(giorno);
    // Giorno della settimana in italiano
    String giornoSettimana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

    boolean visitaProgrammato = visiteMap.values().stream()
        .anyMatch(v -> v.getData() != null && v.getData().equals(data));

    boolean tipoVisitaConsentito = tipiVisitaVolontario.stream()
        .anyMatch(tipo -> isTipoVisitaProgrammabileInGiorno(tipo, data.getDayOfWeek().toString()));

    boolean disponibile = !visitaProgrammato && tipoVisitaConsentito;

    if (disponibile) {
        System.out.printf("%02d\t%s%n", giorno, giornoSettimana);
        giorniDisponibili.add(giorno);
    }
}

    if (giorniDisponibili.isEmpty()) {
        System.out.println("Non ci sono giorni disponibili per dichiarare la disponibilità.");
        return;
    }

    System.out.println("Inserisci le date (formato: DD) separate da virgola tra quelle disponibili:");
    String input = InputDati.leggiStringaNonVuota("Date: ");
    String[] dateArray = input.split(",");
    List<String> dateDisponibili = new ArrayList<>();
    for (String data : dateArray) {
        String trimmed = data.trim();
        try {
            int giorno = Integer.parseInt(trimmed);
            if (!giorniDisponibili.contains(giorno)) {
                System.out.println("Il giorno " + trimmed + " non è disponibile.");
                continue;
            }
            dateDisponibili.add(trimmed);
        } catch (NumberFormatException e) {
            System.out.println("Formato data non valido: " + trimmed);
        }
    }
    disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
    System.out.println("Disponibilità salvata solo in memoria!");
    System.out.println("Date salvate per " + volontario.getEmail() + ": " + dateDisponibili);
}

// Funzione di esempio: da personalizzare in base alla tua logica di associazione tipo-visita/giorno
private boolean isTipoVisitaProgrammabileInGiorno(String tipoVisita, String giornoSettimana) {
    // Esempio: tutte le visite sono programmabili tutti i giorni tranne DOMENICA
    return !giornoSettimana.equals("SUNDAY");
}

}

