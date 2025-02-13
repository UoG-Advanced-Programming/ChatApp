package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ClientHandler implements Runnable {
    private String name;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Set<String> names;
    private Set<PrintWriter> writers;
    private CoordinatorManager coordinatorManager;

    public ClientHandler(Socket socket, Set<String> names, Set<PrintWriter> writers, CoordinatorManager coordinatorManager) {
        this.socket = socket;
        this.names = names;
        this.writers = writers;
        this.coordinatorManager = coordinatorManager;
    }

    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                out.println("SUBMITNAME");
                name = in.nextLine();
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!name.isEmpty() && !names.contains(name)) {
                        names.add(name);
                        coordinatorManager.assignCoordinator(name, out);
                        break;
                    }
                }
            }

            out.println("COORDINATOR " + coordinatorManager.getCoordinator());
            notifyAllClients("MESSAGE " + name + " has joined");
            writers.add(out);
            out.println("NAMEACCEPTED " + name);

            // 🚀 Send updated user list
            ChatServer.broadcastUserList();

            while (in.hasNextLine()) {
                String input = in.nextLine();
                notifyAllClients("MESSAGE " + name + ": " + input);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            cleanup();
        }
    }

    private void notifyAllClients(String message) {
        for (PrintWriter writer : writers) {
            writer.println(message);
        }
        System.out.println(message);
    }

    private void cleanup() {
        if (out != null) {
            writers.remove(out);
            coordinatorManager.removeUser(out);
        }
        if (name != null) {
            System.out.println(name + " is leaving");
            names.remove(name);
            notifyAllClients("MESSAGE " + name + " has left");
            coordinatorManager.reassignCoordinator(name);

            // 🚀 Send updated user list after user leaves
            ChatServer.broadcastUserList();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
