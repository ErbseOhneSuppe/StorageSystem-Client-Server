import java.io.*;
import java.net.*;

public class SimpleWebserver {

    public void start(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Webserver läuft auf Port " + port);

            while (true) {

                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));

                OutputStream out = client.getOutputStream();

                String request = in.readLine();
                System.out.println("Request: " + request);

                if (request == null) {
                    client.close();
                    continue;
                }

                String[] parts = request.split(" ");
                String fileName = parts[1].substring(1);

                if (fileName.equals("")) {
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
