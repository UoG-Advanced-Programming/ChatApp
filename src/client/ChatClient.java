package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
    private ClientGUI gui;

    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
        this.gui = new ClientGUI(this);
    }

    public void start() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(new ClientHandler(in, gui)).start(); // Listen for messages

            // Request username
            out.println(gui.promptForName());

        } catch (IOException e) {
            gui.showError("Unable to connect to server.");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
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
