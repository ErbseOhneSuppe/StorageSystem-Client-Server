import java.sql.*;

public class DatabaseCommands {

    // ------------------------------ [ Tabellen erstellen ] ------------------------------
    public void createTables() {
        String createUserTable = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INT PRIMARY KEY AUTO_INCREMENT,
            first_name VARCHAR(100),
            last_name VARCHAR(100),
            role VARCHAR(50),
            created_at DATETIME,
            last_login DATETIME,
            password_hash VARCHAR(255)
        );
    """;

        String createStorageTable = """
        CREATE TABLE IF NOT EXISTS storages (
            storage_id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(100),
            location VARCHAR(255),
            type VARCHAR(50),
            manager_id INT,
            last_update DATETIME,
            status VARCHAR(50),
            capacity INT,
            FOREIGN KEY (manager_id) REFERENCES users(user_id)
        );
    """;

        String createItemTable = """
        CREATE TABLE IF NOT EXISTS items (
            item_id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(100),
            quantity INT,
            storage_id INT,
            buy_price FLOAT,
            sell_price FLOAT,
            weight FLOAT,
            last_update DATETIME,
            FOREIGN KEY (storage_id) REFERENCES storages(storage_id)
        );
    """;

        try (Connection conn = DatabaseConnector.connectToDatabase();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUserTable);
            stmt.execute(createStorageTable);
            stmt.execute(createItemTable);

            System.out.println("Tabellen wurden erstellt!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------ [ User erstellen, entfernen, login ] ------------------------------
    public void insertUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, role, created_at, last_login, password_hash) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserFirstName());
            ps.setString(2, user.getUserLastName());
            ps.setString(3, user.getRole().name());
            ps.setObject(4, user.getCreatedAt());
            ps.setObject(5, user.getLastLogin());
            ps.setString(6, user.getPasswordHash());

            ps.executeUpdate();
            System.out.println("User gespeichert");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            System.out.println("User gelöscht");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE first_name = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // true wenn User gefunden

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ------------------------------ [ Storage erstellen & entfernen] ------------------------------
    public void insertStorage(Storage storage) {
        String sql = "INSERT INTO storages (name, location, type, manager_id, last_update, status, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, storage.getStorageName());
            ps.setString(2, storage.getLocation());
            ps.setString(3, storage.getType().name());
            ps.setInt(4, storage.getManager().getUserId());
            ps.setObject(5, storage.getLastUpdate());
            ps.setString(6, storage.getStatus().name());
            ps.setInt(7, storage.getCapacity());

            ps.executeUpdate();
            System.out.println("Storage gespeichert");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStorage(int storageId) {
        String sql = "DELETE FROM storages WHERE storage_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, storageId);
            ps.executeUpdate();

            System.out.println("Storage gelöscht");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------ [ Items erstellen & entfernen] ------------------------------
    public void insertItem(Item item) {
        String sql = "INSERT INTO items (name, quantity, storage_id, buy_price, sell_price, weight, last_update) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getQuantity());
            ps.setInt(3, item.getStorageId());
            ps.setFloat(4, item.getBuyPrice());
            ps.setFloat(5, item.getSellPrice());
            ps.setFloat(6, item.getWeight());
            ps.setObject(7, item.getLastUpdate());

            ps.executeUpdate();
            System.out.println("Item gespeichert");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.executeUpdate();

            System.out.println("Item gelöscht");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
