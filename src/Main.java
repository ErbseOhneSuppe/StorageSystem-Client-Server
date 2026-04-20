
public class Main {
    public static void main(String[] args) {

        // DB verbindung testen
        DatabaseConnector db = new DatabaseConnector();
        db.connect();

        // Tabellen erstellen
        DatabaseCommands dbCommands = new DatabaseCommands();
        dbCommands.createTables();

        // Webserver in einem eigenen Thread starten
        Thread webThread = new Thread(() -> {
           SimpleWebserver server = new SimpleWebserver();
           server.start(2903);
        });

        webThread.start();

        System.out.println("System gestartet: DB + Webserver laufen");
    }
}