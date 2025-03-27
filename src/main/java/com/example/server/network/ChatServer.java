package com.example.server.network;

import com.example.common.chats.GroupChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;

public class ChatServer {
    private final Map<User, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private final Map<User, ServerHandler> clientHandlers = new ConcurrentHashMap<>();
    private static final String GENERAL_CHAT_ID = "general-chat"; // Fixed ID for the general chat
    private final CoordinatorManager coordinatorManager;

    public ChatServer() {
        // Initialize the coordinator manager with reference to this server
        this.coordinatorManager = new CoordinatorManager(this);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        ChatServer server = new ChatServer();
        try (ServerSocket listener = new ServerSocket(7005)) {
            while (true) {
                Socket socket = listener.accept();
                ServerHandler handler = new ServerHandler(socket, server);
                pool.execute(handler);
            }
        }
    }

    public void addClient(User user, PrintWriter writer, ServerHandler handler) {
        // Notify all clients about the new user
        broadcast(new UserUpdateMessage(user, UserStatus.ONLINE));
        clientWriters.put(user, writer);
        clientHandlers.put(user, handler); // Store the handler
        send(user, new SystemMessage(SystemMessageType.ID_TRANSITION, GENERAL_CHAT_ID));
        for (User activeUser : clientWriters.keySet()) {
            send(user, new UserUpdateMessage(activeUser, UserStatus.ONLINE));
        }
    }

    public void removeClient(User user) {
        clientWriters.remove(user);
        clientHandlers.remove(user);

        // Notify all clients about the user leaving
        broadcast(new UserUpdateMessage(user, UserStatus.OFFLINE));
    }

    /**
     * Finds a user by their ID from the connected clients
     * @param userId The ID of the user to find
     * @return Optional containing the User if found, or empty if not found
     */
    public Optional<User> findUserById(String userId) {
        // Stream through all connected users and find the one with matching ID
        return clientWriters.keySet().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    public String getUserIpAddress(User user) {
        ServerHandler handler = clientHandlers.get(user);
        if (handler != null) {
            return handler.getClientIpAddress();
        }
        return "Unknown";
    }

    public PrintWriter getClient(User user) {
        return clientWriters.get(user);
    }

    /**
     * Selects a random user from the currently connected clients.
     * @return A randomly selected user, or null if no users are available
     */
    public User selectRandomUser() {
        // Get all online users
        List<User> onlineUsers = new ArrayList<>(clientWriters.keySet());

        if (!onlineUsers.isEmpty()) {
            // Choose a random user from the online users
            Random random = new Random();
            return onlineUsers.get(random.nextInt(onlineUsers.size()));
        } else {
            // Return null without logging - CoordinatorManager will handle messaging
            return null;
        }
    }

    // Expose the client writers map for coordinator management
    public Map<User, PrintWriter> getClientWriters() {
        return clientWriters;
    }

    public CoordinatorManager getCoordinatorManager() {
        return coordinatorManager;
    }

    public User getCoordinator() {
        return coordinatorManager.getCoordinator();
    }

    public void broadcast(Communication message) {
        for (PrintWriter writer : clientWriters.values()) {
            writer.println(MessageSerializer.serialize(message));
        }
    }

    public void send(User user, Communication message) {
        PrintWriter writer = clientWriters.get(user);
        if (writer != null) {
            writer.println(MessageSerializer.serialize(message));
        }
    }
}
