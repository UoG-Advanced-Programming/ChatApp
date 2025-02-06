package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 6000;
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static ClientHandler coordinator = null;
    private static int clientCounter = 1; // Unique ID generator

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            startHeartbeatChecker(); // ✅ Start periodic client connection checks

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept new client connection
                String clientID = "Client-" + clientCounter++; // Generate unique ID
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientID);

                clients.add(clientHandler); // Add client to list
                
                // Elect first client as coordinator
                if (coordinator == null) {
                    coordinator = clientHandler;
                    coordinator.setCoordinator(true);
                    System.out.println("Coordinator assigned: " + coordinator.getClientID());
                }

                new Thread(clientHandler).start(); // Run client in a separate thread
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // ✅ Send a private message to a specific client
    public static void sendPrivateMessage(String senderID, String targetClientID, String message) {
        for (ClientHandler client : clients) {
            if (client.getClientID().equals(targetClientID)) {
                client.sendMessage("[Private from " + senderID + "]: " + message);
                return;
            }
        }
        // Notify sender if the target client is not found
        for (ClientHandler client : clients) {
            if (client.getClientID().equals(senderID)) {
                client.sendMessage("Client " + targetClientID + " not found.");
                return;
            }
        }
    }

    // ✅ Periodically check client connections
    public static void startHeartbeatChecker() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(20000); // Check every 20 seconds
                    for (ClientHandler client : clients) {
                        try {
                            client.sendMessage("heartbeat"); // Send a heartbeat message
                        } catch (Exception e) {
                            // If sending fails, assume the client is disconnected
                            removeClient(client);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ✅ Broadcast message to all clients
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // ✅ Remove a client when it disconnects
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println(client.getClientID() + " disconnected.");

        // If the coordinator left, assign a new one
        if (client == coordinator) {
            assignNewCoordinator();
        }
    }

    // ✅ Assign a new coordinator if the current one disconnects
    private static void assignNewCoordinator() {
        if (!clients.isEmpty()) {
            coordinator = clients.get(0);
            coordinator.setCoordinator(true);
            System.out.println("New Coordinator assigned: " + coordinator.getClientID());
        } else {
            coordinator = null;
            System.out.println("No clients left. No Coordinator assigned.");
        }
    }

    // ✅ This method now checks the actual isCoordinator() method
    public static boolean isCoordinator(ClientHandler client) {
        return client.isCoordinator();
    }

    // ✅ Ensures clients receive the correct coordinator ID
    public static String getCoordinatorID() {
        return (coordinator != null) ? coordinator.getClientID() : "No Coordinator Assigned";
    }
}
