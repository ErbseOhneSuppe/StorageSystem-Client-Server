//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // DB verbindung testen
        DatabaseConnector db = new DatabaseConnector();
        db.connect();

        // Webserver in einem eigenen Thread starten
        Thread webThread = new Thread(() -> {
           SimpleWebserver server = new SimpleWebserver();
           server.start(2903);
        });

        webThread.start();

        System.out.println("System gestartet: DB + Webserver laufen");
    }
}