import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class SimpleWebserver {

    public void start(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Webserver läuft auf Port " + port);

            while (true) {

                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));

                OutputStream out = client.getOutputStream();

                String requestLine = in.readLine();
                System.out.println("Request: " + requestLine);

                if (requestLine == null) {
                    client.close();
                    continue;
                }

                String[] parts = requestLine.split(" ");
                String path = parts[1];

                // =====================================================
                // ================ API ROUTES ==========================
                // =====================================================

                // ---------------- LOGIN ----------------
                if (path.equals("/login")) {

                    String body = readBody(in);

                    String username = extract(body, "username");
                    String password = extract(body, "password");

                    DatabaseCommands db = new DatabaseCommands();
                    boolean success = db.login(username, password);

                    System.out.println("Login Result: " + success);

                    sendResponse(out, success ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                // ---------------- GET ITEMS ----------------
                if (path.equals("/items")) {

                    DatabaseCommands db = new DatabaseCommands();
                    String json = db.getAllItemsJson();

                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: application/json\r\n\r\n".getBytes());
                    out.write(json.getBytes());

                    client.close();
                    continue;
                }

                // ---------------- CREATE ITEM ----------------
                if (path.equals("/item/create")) {

                    try {

                        String body = readBody(in);

                        System.out.println("RAW BODY: " + body);

                        String itemName = extract(body, "itemName");
                        String quantityStr = extract(body, "quantity");
                        String storageIdStr = extract(body, "storageId");
                        String buyPriceStr = extract(body, "buyPrice");
                        String sellPriceStr = extract(body, "sellPrice");
                        String weightStr = extract(body, "weight");

                        // ---------------- VALIDATION ----------------
                        if (itemName.isEmpty() || quantityStr.isEmpty() || storageIdStr.isEmpty()) {
                            sendResponse(out, "ERROR: missing fields");
                            client.close();
                            continue;
                        }

                        // ---------------- SAFE PARSE ----------------
                        int quantity = safeInt(quantityStr);
                        int storageId = safeInt(storageIdStr);
                        float buyPrice = safeFloat(buyPriceStr);
                        float sellPrice = safeFloat(sellPriceStr);
                        float weight = safeFloat(weightStr);

                        Item item = new Item(
                                0,
                                itemName,
                                quantity,
                                storageId,
                                buyPrice,
                                sellPrice,
                                weight,
                                LocalDateTime.now()
                        );

                        DatabaseCommands db = new DatabaseCommands();
                        db.insertItem(item);

                        sendResponse(out, "OK");

                    } catch (Exception e) {
                        e.printStackTrace();
                        sendResponse(out, "ERROR");
                    }

                    client.close();
                    continue;
                }

                //---------------- EDIT ITEM ----------------
                if (path.equals("/item/update")) {

                    String body = readBody(in);

                    Item item = new Item(
                            Integer.parseInt(extract(body, "itemId")),
                            extract(body, "itemName"),
                            safeInt(extract(body, "quantity")),
                            safeInt(extract(body, "storageId")),
                            safeFloat(extract(body, "buyPrice")),
                            safeFloat(extract(body, "sellPrice")),
                            safeFloat(extract(body, "weight")),
                            LocalDateTime.now()
                    );

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.updateItem(item);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");
                    client.close();
                    continue;
                }

                // ---------------- DELETE ITEM ----------------
                if (path.startsWith("/item/delete")) {

                    String query = path.split("\\?")[1];
                    int id = Integer.parseInt(query.split("=")[1]);

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.deleteItem(id);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                // ---------------- GET STORAGE ----------------
                if (path.equals("/storages")) {

                    DatabaseCommands db = new DatabaseCommands();
                    String json = db.getAllStoragesJson();

                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: application/json\r\n\r\n".getBytes());
                    out.write(json.getBytes());

                    client.close();
                    continue;
                }

                // ---------------- CREATE STORAGE ----------------
                if (path.equals("/storage/create")) {

                    String body = readBody(in);

                    Storage storage = new Storage(
                            0,
                            extract(body, "storageName"),
                            extract(body, "location"),
                            Storage.StorageType.NORMAL,
                            new User(1, "admin", "admin", User.Role.ADMIN, null, null, ""),
                            LocalDateTime.now(),
                            Storage.StorageStatus.AKTIV,
                            safeInt(extract(body, "capacity"))
                    );

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.insertStorage(storage);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                //---------------- EDIT STORAGE ----------------

                // ---------------- CHECK IF STORAGE HAS ITEM ----------------
                if (path.startsWith("/storage/hasItems")) {

                    int id = Integer.parseInt(path.split("=")[1]);

                    DatabaseCommands db = new DatabaseCommands();
                    boolean hasItems = db.storageHasItems(id);

                    sendResponse(out, hasItems ? "YES" : "NO");

                    client.close();
                    continue;
                }

                // ---------------- DELETE STORAGE ----------------
                if (path.startsWith("/storage/delete")) {

                    String query = path.split("\\?")[1];
                    int id = Integer.parseInt(query.split("=")[1]);

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.deleteStorage(id);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                // ---------------- GET USER ----------------
                if (path.equals("/users")) {

                    DatabaseCommands db = new DatabaseCommands();
                    String json = db.getAllUsersJson();

                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: application/json\r\n\r\n".getBytes());
                    out.write(json.getBytes());

                    client.close();
                    continue;
                }

                // ---------------- CREATE USER ----------------
                if (path.equals("/user/create")) {

                    String body = readBody(in);

                    User user = new User(
                            0,
                            extract(body, "firstName"),
                            extract(body, "lastName"),
                            User.Role.EMPLOYEE,
                            LocalDateTime.now(),
                            null,
                            extract(body, "password")
                    );

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.insertUser(user);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                //---------------- EDIT USER ----------------
                if (path.equals("/user/update")) {

                    String body = readBody(in);

                    User user = new User(
                            Integer.parseInt(extract(body, "userId")),
                            extract(body, "firstName"),
                            extract(body, "lastName"),
                            User.Role.valueOf(extract(body, "role")),
                            null,
                            null,
                            extract(body, "password")
                    );

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.updateUser(user);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");
                    client.close();
                    continue;
                }

                // ---------------- DELETE USER ----------------
                if (path.startsWith("/user/delete")) {

                    String query = path.split("\\?")[1];
                    int id = Integer.parseInt(query.split("=")[1]);

                    DatabaseCommands db = new DatabaseCommands();
                    int rows = db.deleteUser(id);

                    sendResponse(out, rows > 0 ? "OK" : "FAIL");

                    client.close();
                    continue;
                }

                // =====================================================
                // ================ FILE SERVER =========================
                // =====================================================

                String fileName = path.substring(1);

                if (fileName.isEmpty()) {
                    fileName = "index.html";
                }

                File file = new File(fileName);

                if (file.exists()) {

                    FileInputStream fis = new FileInputStream(file);

                    String contentType = "text/html";

                    if (fileName.endsWith(".css")) {
                        contentType = "text/css";
                    } else if (fileName.endsWith(".js")) {
                        contentType = "application/javascript";
                    }

                    out.write(("HTTP/1.1 200 OK\r\n").getBytes());
                    out.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes());

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    fis.close();

                } else {

                    String error = "<h1>404 Not Found</h1>";

                    out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                    out.write("Content-Type: text/html\r\n\r\n".getBytes());
                    out.write(error.getBytes());
                }

                client.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // ================== HELPERS ==========================
    // =====================================================

    private String readBody(BufferedReader in) throws Exception {

        String line;
        int contentLength = 0;

        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        char[] bodyChars = new char[contentLength];
        in.read(bodyChars);

        return new String(bodyChars);
    }

    private String extract(String body, String key) {

        String stringPattern = "\"" + key + "\":\"";
        String numberPattern = "\"" + key + "\":";

        int start;

        // String value
        if (body.contains(stringPattern)) {
            start = body.indexOf(stringPattern) + stringPattern.length();
            int end = body.indexOf("\"", start);
            return body.substring(start, end);
        }

        // Number value
        if (body.contains(numberPattern)) {
            start = body.indexOf(numberPattern) + numberPattern.length();
            int end = body.indexOf(",", start);

            if (end == -1) end = body.indexOf("}", start);

            return body.substring(start, end).trim();
        }

        return "";
    }

    private void sendResponse(OutputStream out, String msg) throws Exception {
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Type: text/plain\r\n\r\n".getBytes());
        out.write(msg.getBytes());
    }

    private int safeInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private float safeFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }
}