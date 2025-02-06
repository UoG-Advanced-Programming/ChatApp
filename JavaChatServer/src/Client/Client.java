package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Localhost
    private static final int SERVER_PORT = 6000; // Server Port
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.println("Connected to server on port " + SERVER_PORT);
            System.out.println("Type a message to broadcast or use /msg <clientID> <message> to send private messages.");

            // Start a thread to listen for server messages
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("\n[Server]: " + serverMessage);
                        System.out.print("> "); // Keeps input prompt visible
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost.");
                }
            });
            listenerThread.start();

            // Read user input and send to server
            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();

                //  Graceful exit command
                if (message.equalsIgnoreCase("/exit")) {
                    out.println("/exit"); // Notify server
                    System.out.println("Disconnecting...");
                    closeConnection(); // Proper shutdown
                    break;
                }

                //  Private messaging format: /msg <clientID> <message>
                if (message.startsWith("/msg ")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length < 3) {
                        System.out.println("Invalid format. Please use: /msg <clientID> <message>");
                        continue;
                    }
                }

                //  Send the message to the server
                out.println(message);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    //  Proper shutdown function
    private static void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
