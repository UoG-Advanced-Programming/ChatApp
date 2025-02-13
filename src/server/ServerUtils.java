package server;

import java.io.PrintWriter;
import java.util.Set;

public class ServerUtils {

    // 🚀 Sends a message to all connected clients
    public static void broadcastMessage(Set<PrintWriter> writers, String message) {
        for (PrintWriter writer : writers) {
            writer.println(message);
        }
        System.out.println(message);
    }

    // 🚀 Generates and sends the updated user list
    public static void broadcastUserList(Set<String> names, Set<PrintWriter> writers) {
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