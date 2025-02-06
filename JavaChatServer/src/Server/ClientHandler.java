package Server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientID;
    private boolean isCoordinator = false;

    public ClientHandler(Socket socket, String clientID) {
        this.socket = socket;
        this.clientID = clientID;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println(clientID + " connected.");
            out.println("Welcome, " + clientID);

            if (Server.isCoordinator(this)) {
                setCoordinator(true);
            } else {
                out.println("Current Coordinator: " + Server.getCoordinatorID());
            }

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    System.out.println(clientID + " has disconnected.");
                    break; // Exit loop and disconnect client
                }

                //  Handle private messages
                if (message.startsWith("/msg ")) {
                    String[] parts = message.split(" ", 3); // Split into 3 parts: /msg, clientID, message
                    if (parts.length == 3) {
                        String targetClientID = parts[1];
                        String privateMessage = parts[2];
                        Server.sendPrivateMessage(clientID, targetClientID, privateMessage);
                    } else {
                        out.println("Invalid private message format. Use: /msg <clientID> <message>");
                    }
                } else {
                    //  Broadcast the message
                    System.out.println(clientID + ": " + message);
                    Server.broadcastMessage(clientID + ": " + message, this);
                }
            }
        } catch (IOException e) {
            System.err.println(clientID + " disconnected unexpectedly.");
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        } else {
            System.err.println("Error: PrintWriter is null for " + clientID);
        }
    }

    public String getClientID() {
        return clientID;
    }

    public void setCoordinator(boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
        sendMessage("You are now the Coordinator.");
    }

    public boolean isCoordinator() {
        return isCoordinator;
    }

    private void disconnect() {
        try {
            Server.removeClient(this);
            if (socket != null) {
                socket.close();
            }
            System.out.println("User " + clientID + " has disconnected."); //  Now shows in the server console
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
