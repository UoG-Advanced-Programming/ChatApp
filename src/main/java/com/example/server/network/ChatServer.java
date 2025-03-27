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
import java.util.Random;
import java.util.concurrent.*;

public class ChatServer {
    private final Map<User, PrintWriter> clientWriters = new ConcurrentHashMap<>();
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
                pool.execute(new ServerHandler(socket, server));
            }
        }
    }

    public void addClient(User user, PrintWriter writer) {
        // Notify all clients about the new user
        broadcast(new UserUpdateMessage(user, UserStatus.ONLINE));
        clientWriters.put(user, writer);

        // Assign user as coordinator if needed
        coordinatorManager.assignCoordinator(user);

        send(user, new SystemMessage(SystemMessageType.ID_TRANSITION, GENERAL_CHAT_ID));
        for (User activeUser : clientWriters.keySet()) {
            send(user, new UserUpdateMessage(activeUser, UserStatus.ONLINE));
        }
    }

    public void removeClient(User user) {
        // First remove the client from the map BEFORE reassigning the coordinator
        clientWriters.remove(user);

        // Then handle coordinator reassignment if the user was the coordinator
        coordinatorManager.reassignCoordinator(user);

        // Notify all clients about the user leaving
        broadcast(new UserUpdateMessage(user, UserStatus.OFFLINE));
    }

    public PrintWriter getClient(User user) {
        return clientWriters.get(user);
    }

    /**
     * Selects a random user from the currently connected clients.
     * @param excludeUser User to exclude from selection (can be null)
     * @return A randomly selected user, or null if no users are available
     */
    public User selectRandomUser(User excludeUser) {
        // Get all online users
        List<User> onlineUsers = new ArrayList<>(clientWriters.keySet());

        // Remove the excluded user from consideration if provided
        if (excludeUser != null) {
            onlineUsers.remove(excludeUser);
        }

        if (!onlineUsers.isEmpty()) {
            // Choose a random user from the online users
            Random random = new Random();
            return onlineUsers.get(random.nextInt(onlineUsers.size()));
        } else {
            // Return null without logging - CoordinatorManager will handle messaging
            return null;
        }
    }

    // Overloaded method for backward compatibility
    public User selectRandomUser() {
        return selectRandomUser(null);
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

    private void send(User user, Communication message) {
        PrintWriter writer = clientWriters.get(user);
        if (writer != null) {
            writer.println(MessageSerializer.serialize(message));
        }
    }
}