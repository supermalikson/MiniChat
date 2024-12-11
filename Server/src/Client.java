import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame {

    public static final int SERVER_PORT = 5555;

    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            this.name = name;

        } catch (IOException e) {
            System.out.println("Client creation failed");
        }
    }

    public void connectionInit() {
        out.print(name);
        out.println();
        out.flush();

        Scanner scanner = new Scanner(System.in);
        while (socket.isConnected()) {
            String message = scanner.nextLine();
            out.println(name + ": " + message);
            out.flush();
        }
        disconnect();
    }

    public void listenForMessages() {
        new Thread(() -> {
            String message;
            while (socket.isConnected()) {
                try {
                    message = in.readLine();
                    System.out.println(message);

                } catch (IOException e) {
                    disconnect();
                    break;
                }
            }
        }).start();
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scanner.nextLine();

        try {
            Socket socket = new Socket("localhost", Client.SERVER_PORT);
            Client client = new Client(socket, name);
            System.out.println("Connected");
            client.listenForMessages();
            client.connectionInit();

        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }

    public void disconnect() {
        System.out.println("Connection closed");
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
