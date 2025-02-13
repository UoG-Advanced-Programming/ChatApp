package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.*;

public class ChatServer {
    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static CoordinatorManager coordinatorManager = new CoordinatorManager(writers);

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(7005)) {
            while (true) {
                Socket socket = listener.accept();
                pool.execute(new ClientHandler(socket, names, writers, coordinatorManager));
            }
        }
    }

    // 🚀 Sends the updated user list to all clients
    public static void broadcastUserList() {
        StringBuilder userListMessage = new StringBuilder("USERS ");
        synchronized (names) {
            for (String name : names) {
                userListMessage.append(name).append(",");
            }
        }
        String userList = userListMessage.toString();
        for (PrintWriter writer : writers) {
            writer.println(userList);
        }
    }
}