package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class ChatClient {
    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
    private ClientGUI gui;
    private Set<String> connectedUsers = new HashSet<>();

    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
        this.gui = ClientGUI.getInstance(this);
    }

    public void start() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 7005);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(new ClientHandler(in, gui, this)).start();

            out.println(promptForName());

        } catch (IOException e) {
            gui.showError("Unable to connect to server.");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void updateUserList(Set<String> users) {
        this.connectedUsers = users;
        gui.updateUsers(users);
    }

    public void disconnect() {
        sendMessage("/quit");
        System.exit(0);
    }

    private String promptForName() {
        return JOptionPane.showInputDialog(
                null,
                "Choose a screen name:",
                "Screen Name Selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        ChatClient client = new ChatClient(args[0]);
        client.start();
    }
}
