package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private String name;
    private String ip;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Set<String> names;
    private Set<PrintWriter> writers;
    private CoordinatorManager coordinatorManager;
    private static ConcurrentHashMap<String, PrintWriter> userWriters = new ConcurrentHashMap<>(); // Store active users

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
                        userWriters.put(name, out); // Store user PrintWriter for private messages
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
                } else if (input.toLowerCase().startsWith("private ")) { 
                    handlePrivateMessage(input);
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

    private void handlePrivateMessage(String input) {
        try {
            // Format: PRIVATE (username) (message)
            String[] parts = input.split(" ", 3);
            if (parts.length < 3) {
                out.println("MESSAGE Usage: PRIVATE (username) (message)");
                return;
            }

            String targetName = parts[1].trim();
            String privateMessage = parts[2].trim();

            PrintWriter targetWriter = userWriters.get(targetName);
            PrintWriter senderWriter = userWriters.get(name);

            if (targetWriter != null) {
                // Send message to recipient
                targetWriter.println("PRIVATE " + name + " " + privateMessage);
                targetWriter.flush();

                senderWriter.println("PRIVATE " + name + " " + privateMessage);
                senderWriter.flush();

                // Log message to console for debugging
                System.out.println("[PRIVATE] " + name + " → " + targetName + ": " + privateMessage);
            } else {
                out.println("MESSAGE User " + targetName + " is not online or does not exist.");
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Error sending private message: " + e.getMessage());
            out.println("MESSAGE An error occurred while sending the private message.");
            out.flush();
        }
    }

    private void notifyAllClients(String message) {
        for (PrintWriter writer : writers) {
            writer.println(message);
            writer.flush();
        }
        System.out.println(message);
    }

    private void cleanup() {
        if (out != null) {
            writers.remove(out);
            userWriters.remove(name); // Remove user from private messaging
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
