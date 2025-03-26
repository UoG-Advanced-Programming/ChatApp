package com.example.server.network;

import com.example.common.chats.GroupChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

public class ChatServer {
    private final GroupChat generalChat = new GroupChat("General Chat");
    private final Map<User, PrintWriter> clientWriters = new ConcurrentHashMap<>();
    private User coordinator = null; // Track the current coordinator

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
        clientWriters.put(user, writer);
        generalChat.addParticipant(user);
        send(user, new SystemMessage(SystemMessageType.CHAT_INIT, generalChat));

        // Notify all clients about the new user
        broadcast(new UserUpdateMessage(user, UserStatus.ONLINE));

        // Log coordinator status if applicable
        if (user.getIsCoordinator()) {
            coordinator = user;
            System.out.println("COORDINATOR: " + user.getUsername() + " has joined as coordinator");
        }
    }

    public void removeClient(User user) {
        clientWriters.remove(user);
        generalChat.removeParticipant(user);

        // Check if this was the coordinator
        boolean wasCoordinator = (coordinator != null && coordinator.getId().equals(user.getId()));

        // Notify all clients about the user leaving
        broadcast(new UserUpdateMessage(user, UserStatus.OFFLINE));

        // If the coordinator left, set the field to null
        if (wasCoordinator) {
            System.out.println("COORDINATOR: " + user.getUsername() + " (coordinator) has left the chat");
            coordinator = null;
        }
    }

    public PrintWriter getClient(User user) {
        return clientWriters.get(user);
    }

    // Expose the client writers map for coordinator management
    public Map<User, PrintWriter> getClientWriters() {
        return clientWriters;
    }

    public User getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(User newCoordinator) {
        if (newCoordinator != null) {
            // First, reset any existing coordinator
            if (coordinator != null) {
                coordinator.setIsCoordinator(false);
            }

            // Set the new coordinator
            coordinator = newCoordinator;
            coordinator.setIsCoordinator(true);
            System.out.println("COORDINATOR: " + coordinator.getUsername() + " is now the coordinator");
        }
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