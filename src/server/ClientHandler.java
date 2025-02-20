package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ClientHandler implements Runnable {
    private String name;
    private String ip;
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

            // Store IP address
            ip = socket.getInetAddress().getHostAddress();

            while (true) {
                out.println("SUBMITNAME");
                name = in.nextLine();
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!name.isEmpty() && !names.contains(name)) {
                        names.add(name);
                        coordinatorManager.assignCoordinator(name, out, ip);
                        break;
                    }
                }
            }

            out.println("COORDINATOR " + coordinatorManager.getCoordinator());
            notifyAllClients("MESSAGE " + name + " has joined from IP: " + ip);
            writers.add(out);
            out.println("NAMEACCEPTED " + name);

            ChatServer.broadcastUserList();

            while (in.hasNextLine()) {
                String input = in.nextLine();
                if (input.toLowerCase().startsWith("/getdetails ")) {
                    String targetName = input.substring("/getdetails ".length()).trim();
                    coordinatorManager.requestDetails(name, targetName, out);
                } else if (input.startsWith("APPROVE ")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        String requesterName = parts[1];
                        String targetName = parts[2];
                        coordinatorManager.handleDetailsResponse(requesterName, targetName, true);
                    }
                } else if (input.startsWith("DENY ")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        String requesterName = parts[1];
                        String targetName = parts[2];
                        coordinatorManager.handleDetailsResponse(requesterName, targetName, false);
                    }
                } else {
                    notifyAllClients("MESSAGE " + name + ": " + input);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            System.out.println(name + " is leaving.");
            names.remove(name);
            notifyAllClients("MESSAGE " + name + " has left.");
            coordinatorManager.reassignCoordinator(name);

            ChatServer.broadcastUserList();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
