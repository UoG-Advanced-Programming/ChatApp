package com.example.server;

import com.example.models.Communication;
import com.example.models.MessageSerializer;
import com.example.models.User;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServer {
    private Map<User, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        ChatServer server = new ChatServer(); // Create an instance here
        try (ServerSocket listener = new ServerSocket(7005)) {
            while (true) {
                Socket socket = listener.accept();
                pool.execute(new ServerHandler(socket, server));
            }
        }
    }

    public void addClient(User user, PrintWriter writer) {
        clientWriters.put(user, writer);
    }

    public void removeClient(User user) {
        clientWriters.remove(user);
    }

    public PrintWriter getClient(User user) {
        return clientWriters.get(user);
    }
}
