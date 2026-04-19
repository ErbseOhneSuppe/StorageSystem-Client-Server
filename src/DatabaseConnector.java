import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnector {

    // WICHTIG: BEI ÄNDERUNG DER DATENBANK LOCATION HIER ÄNDERN!!!!
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/storagesystem_shema";
    private static final String USER = "root";
    private static final String PASSWORD = ""; //password123

    public void connect(){
        Connection connection = null;
        Statement stm = null;

        try {
            connection = connectToDatabase();
            stm = connection.createStatement();

            System.out.println("Verbindung Erfolgreich");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(stm, connection);
        }
    }

    // Verbindung zur Datenbank herstellen
    public static Connection connectToDatabase() throws Exception {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Verbindung mit der Datenbank ist erfolgreich");
        return connection;
    }

    // Ressourcen schließen
    public static void closeResources(Statement stm, Connection connection) {
        try {
            if (stm != null) {
                stm.close();
            }
            if (connection != null) {
                connection.close();
            }
            System.out.println("Verbindung wurde geschlossen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
