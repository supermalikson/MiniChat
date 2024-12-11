import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {

    private ServerSocket server;
    private int port;
    private String serverName;
    private static List<String> bannedPhrases;
    private BufferedReader in;

    public Server() {
        try {
            bannedPhrases = new ArrayList<>();
            in = new BufferedReader(new FileReader("Server\\src\\ServerConfig"));
            System.out.println("Ready");


            serverName = in.readLine();
            port = Integer.parseInt(in.readLine());

            String line = in.readLine(); //Read useless line
            while ((line = in.readLine()) != null) {
                bannedPhrases.add(line);
            }

            System.out.println("Ready 2");

            server = new ServerSocket(port);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serverStart() {
        try {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                ClientListener client = new ClientListener(socket);

                Thread thread = new Thread(client);
                thread.start();


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getPhrases() {
        return bannedPhrases;
    }

    public static void main(String[] args) {
        Server echoServer = new Server();

        echoServer.serverStart();
    }
}
