import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientListener implements Runnable{

    private static ArrayList<ClientListener> clients = new ArrayList<>();
    private static List<String> bannedPhrases;

    private String name;
    private int port;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<String> sendTo = new ArrayList<>();


    static {
        bannedPhrases = Server.getPhrases();
    }

    public ClientListener(Socket client) {
        try {

            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());

            name = in.readLine();
            port = client.getPort();
            clients.add(this);

            System.out.println(name + " has connected!");

            //Greetings
            out.println("Welcome to the chat " + name + "!\n" +
                    "By default your messages are visible for everyone.\n" +
                    "In order to send messages directly to user or many users, type and send 'to: username' (or 'to: username username') first,\n" +
                    "only then send your messages. Type 'all' for every user currently online. To exclude person type 'not: username'.\n" +
                    "Type '#banned to get a list of prohibited phrases'");
            out.println("Users online:");
            for (ClientListener c : clients) {
                out.println(c.name);
            }
            out.println();
            out.flush();

            sendMessageToAll("\t\t" + name + " has connected to the server!");
        } catch (IOException e) {
            disconnect();
        }


    }


    public void sendMessageToAll(String message) {
        boolean legal = checkMessageForBannedPhrases(message);
        if (!legal) {
            out.print("SERVER: You are trying to send a message containing prohibited phrases. Your message will not be delivered!!!");
            out.println();
            out.flush();
        } else {
            for (ClientListener c : clients) {
                try {
                    if (!c.name.equals(name)) {
                        c.out.println(message);
                        c.out.flush();
                    }
                } catch (Exception e) {
                    disconnect();
                }
            }
        }
    }

    public void sendMessageToSpecificPerson(String message) {
        for (ClientListener c : clients) {
            for (String sendToName : sendTo) {
                if (c.name.equals(sendToName)) {
                    c.out.println(sendTo.size() > 1 ? "(group)" : "(private)" + message);
                    c.out.flush();
                }
            }
        }
    }

    public void sendMsg(String message) {
        String[] s = message.split(" ");
        switch (s[1]) {
            case "to:" -> {
                sendTo.clear();
                if (!s[2].equals("all")) {
                    for (int i = 2; i < s.length; i++) sendTo.add(s[i]);
                }
            }
            case "not:" -> {
                sendTo.clear();
                for (ClientListener c : clients) {
                    if (!c.name.equals(name))
                    if (!c.name.equals(name))
                    sendTo.add(c.name);
                }
                for (int i = 2; i < s.length; i++) {
                    for (String str : sendTo) {
                        if (str.equals(s[i])) sendTo.remove(s[i]);
                    }
                }
            }
            case "#banned" -> {
                out.println("The list of banned phrases");
                for (String b : bannedPhrases) {
                    out.println(b);
                }
                out.flush();
            }
            default -> {
                if (sendTo.isEmpty())
                    sendMessageToAll(message);
                else {
                    sendMessageToSpecificPerson(message);
                }
            }
        }
    }

    private boolean checkMessageForBannedPhrases(String message) {
        for (String bannedPhrase : bannedPhrases) {
            Pattern phrase = Pattern.compile(bannedPhrase, Pattern.CASE_INSENSITIVE);
            Matcher m = phrase.matcher(message);
            if (m.find())
                return false;
        }
        return true;
    }


    @Override
    public void run() {
        String message;
        while (client.isConnected()) {
            try {
                message = in.readLine();

                if (!message.isEmpty()) {
                    boolean legal = checkMessageForBannedPhrases(message);
                    if (!legal) {
                        out.print("SERVER: You are trying to send a message containing prohibited phrases. Your message will not be delivered!!!");
                        out.println();
                        out.flush();
                    } else {
                        sendMsg(message);
                    }
                }
            } catch (IOException e) {
                disconnect();
                break;
            }
        }
    }

    public void disconnect() {
        sendMessageToAll("\t\t" + name + " has disconnected. :(");
        System.out.println(name + " has disconnected");
        clients.remove(this);

        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
