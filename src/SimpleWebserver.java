import java.io.*;
import java.net.*;

// http://localhost:2903/index.html
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

                // ---------------- LOGIN ----------------
                if (path.equals("/login")) {

                    DatabaseCommands db = new DatabaseCommands();

                    String line;
                    int contentLength = 0;

                    // Header lesen
                    while (!(line = in.readLine()).isEmpty()) {
                        if (line.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(line.split(":")[1].trim());
                        }
                    }

                    // Body lesen
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars);
                    String body = new String(bodyChars);

                    System.out.println("Body: " + body);

                    String username = body.split("\"username\":\"")[1].split("\"")[0];
                    String password = body.split("\"password\":\"")[1].split("\"")[0];

                    boolean success = db.login(username, password);

                    System.out.println("Login Result: " + success);

                    String response = success ? "OK" : "FAIL";

                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: text/plain\r\n\r\n".getBytes());
                    out.write(response.getBytes());

                    client.close();
                    continue;
                }

                // ---------------- FILES ----------------
                String fileName = path.substring(1);

                if (fileName.isEmpty()) {
                    fileName = "index.html";
                }

                File file = new File(fileName);

                if (file.exists()) {

                    BufferedReader fileReader = new BufferedReader(new FileReader(file));

                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write("Content-Type: text/html\r\n\r\n".getBytes());

                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        out.write(line.getBytes());
                    }

                    fileReader.close();

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
}