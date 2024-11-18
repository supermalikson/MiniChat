import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {

    private ServerSocket server;
    private int port;
    private String serverName;
    private List<String> bannedPhrases;
    private BufferedReader in;

    public void createGUI() {
        new JFrame("MainServer");
        setResizable(false);
        setSize(new Dimension(800, 600));
        getContentPane().setBackground(new Color(204, 241, 169));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridLayout());
        setVisible(true);
    }


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

    public void serverTurnUp() {

    }

    public String getPhrase() {
        return bannedPhrases.getFirst();
    }

    public static void main(String[] args) {
        Server echoServer = new Server();
        echoServer.createGUI();
        while (true) {


            try {

                Socket client = echoServer.server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String name = in.readLine();
                ClientListener nClient = new ClientListener(name, client);
                Thread thread = new Thread(nClient);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    
}
