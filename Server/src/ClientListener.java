import java.net.Socket;
import java.util.ArrayList;

public class ClientListener implements Runnable{

    private static ArrayList<ClientListener> clients;
    private String name;
    private int port;
    private Socket client;

    public String getName() { return name;}

    public ClientListener(String name, Socket client) {
        if (!clientExists(name, client))  {
            this.name = name;
            this.client = client;
            port = client.getPort();
            clients.add(this);
        }

    }

    public void changeSocket(Socket socket) {client = socket;}

    public boolean clientExists(String name, Socket client) {
        boolean exist = false;

        for (ClientListener existing : clients) {
            if (name.equals(existing.getName())) {
                exist = true;
                existing.changeSocket(client);
            }
        }

        return exist;
    }

    @Override
    public void run() {
        while (true) {


        }
    }
}
