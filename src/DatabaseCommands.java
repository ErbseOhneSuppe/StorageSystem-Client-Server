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

    // ------------------------------ [ User erstellen, entfernen, login, JSON ] ------------------------------
    public int insertUser(User user) {

        String sql = "INSERT INTO users (first_name, last_name, role, created_at, last_login, password_hash) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserFirstName());
            ps.setString(2, user.getUserLastName());
            ps.setString(3, user.getRole().name());
            ps.setObject(4, user.getCreatedAt());
            ps.setObject(5, user.getLastLogin());
            ps.setString(6, user.getPasswordHash());

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateUser(User user) {

        String sql = """
        UPDATE users 
        SET first_name = ?, last_name = ?, role = ?, password_hash = ?
        WHERE user_id = ?
    """;

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserFirstName());
            ps.setString(2, user.getUserLastName());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getPasswordHash());
            ps.setInt(5, user.getUserId());

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteUser(int id) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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

    public User getUserByUsername(String username) {

        String sql = "SELECT * FROM users WHERE first_name = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        User.Role.valueOf(rs.getString("role")),
                        rs.getObject("created_at", java.time.LocalDateTime.class),
                        rs.getObject("last_login", java.time.LocalDateTime.class),
                        rs.getString("password_hash")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAllUsersJson() {

        String sql = "SELECT * FROM users";

        StringBuilder json = new StringBuilder("[");

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                json.append("{")
                        .append("\"userId\":").append(rs.getInt("user_id")).append(",")
                        .append("\"firstName\":\"").append(rs.getString("first_name")).append("\",")
                        .append("\"lastName\":\"").append(rs.getString("last_name")).append("\",")
                        .append("\"role\":\"").append(rs.getString("role")).append("\",")
                        .append("\"password\":\"").append(rs.getString("password_hash")).append("\"")
                        .append("},");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }

        json.append("]");
        return json.toString();
    }

    // ------------------------------ [ Storage erstellen, entfernen, JSON ] ------------------------------
    public int insertStorage(Storage storage) {

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

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteStorage(int storageId) {

        String sql = "DELETE FROM storages WHERE storage_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, storageId);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean storageHasItems(int storageId) {

        String sql = "SELECT COUNT(*) FROM items WHERE storage_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, storageId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getAllStoragesJson() {

        String sql = "SELECT * FROM storages";

        StringBuilder json = new StringBuilder("[");

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                json.append("{")
                        .append("\"storageId\":").append(rs.getInt("storage_id")).append(",")
                        .append("\"name\":\"").append(rs.getString("name")).append("\",")
                        .append("\"location\":\"").append(rs.getString("location")).append("\",")
                        .append("\"type\":\"").append(rs.getString("type")).append("\",")
                        .append("\"status\":\"").append(rs.getString("status")).append("\",")
                        .append("\"capacity\":").append(rs.getInt("capacity"))
                        .append("},");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }

        json.append("]");
        return json.toString();
    }

    // ------------------------------ [ Items erstellen, entfernen, JSON ] ------------------------------
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

            int rows = ps.executeUpdate();

            System.out.println("INSERT RESULT ROWS: " + rows);

        } catch (Exception e) {
            System.out.println("DB INSERT ERROR:");
            e.printStackTrace();
        }
    }

    // Item entfernen
    public int deleteItem(int itemId) {

        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Alle items in json packen das man diese dann auslesen kann für die Seite
    public String getAllItemsJson() {

        StringBuilder json = new StringBuilder();
        json.append("[");

        try (Connection conn = DatabaseConnector.connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {

            while (rs.next()) {

                json.append("{")
                        .append("\"itemId\":").append(rs.getInt("item_id")).append(",")
                        .append("\"itemName\":\"").append(rs.getString("name")).append("\",")
                        .append("\"quantity\":").append(rs.getInt("quantity")).append(",")
                        .append("\"storageId\":").append(rs.getInt("storage_id")).append(",")
                        .append("\"buyPrice\":").append(rs.getFloat("buy_price")).append(",")
                        .append("\"sellPrice\":").append(rs.getFloat("sell_price")).append(",")
                        .append("\"weight\":").append(rs.getFloat("weight"))
                        .append("},");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }

        json.append("]");

        return json.toString();
    }
}
