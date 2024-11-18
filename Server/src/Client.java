import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class Client extends JFrame {

    private final int SERVER_PORT = 5555;

    private String name;
    private Socket socket;

    public void createGui() {
        new JFrame("Client");
        setResizable(false);
        setSize(new Dimension(800, 600));
        getContentPane().setBackground(new Color(204, 241, 169));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridLayout());
        setVisible(true);
    }


    public Client() {

    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void changeSocket(Socket socket) {this.socket = socket;}

    public static void main(String[] args) {

    }


}
