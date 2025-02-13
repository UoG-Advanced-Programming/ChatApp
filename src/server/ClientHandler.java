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
    private boolean isMuted = false; // Track if this user is muted
    private static Set<String> mutedUsers = new HashSet<>(); // Track all muted users

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
                        coordinatorManager.assignCoordinator(name);
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

            while (true) {
                String input = in.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
                } else if (input.toLowerCase().startsWith("/msg")) {
                    handlePrivateMessage(input);
                } else if (input.toLowerCase().startsWith("/kick") && coordinatorManager.getCoordinator().equals(name)) {
                    handleKickCommand(input);
                } else if (input.toLowerCase().startsWith("/mute") && coordinatorManager.getCoordinator().equals(name)) {
                    handleMuteCommand(input);
                } else if (input.toLowerCase().startsWith("/unmute") && coordinatorManager.getCoordinator().equals(name)) {
                    handleUnmuteCommand(input);
                } else if (!isMuted && !mutedUsers.contains(name)) {
                    notifyAllClients("MESSAGE " + name + ": " + input);
                } else if (isMuted || mutedUsers.contains(name)) {
                    out.println("MESSAGE You are muted and cannot send messages.");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            cleanup();
        }
    }

    private void handlePrivateMessage(String input) {
        String[] parts = input.split(" ", 3);
        if (parts.length == 3) {
            String recipient = parts[1];
            String privateMessage = parts[2];
            if (names.contains(recipient)) {
                for (PrintWriter writer : writers) {
                    writer.println("PRIVATE " + name + ": " + privateMessage);
                }
                System.out.println("Private message from " + name + " to " + recipient + ": " + privateMessage);
            } else {
                out.println("MESSAGE Recipient " + recipient + " not found.");
            }
        } else {
            out.println("MESSAGE Invalid private message format. Use /msg <recipient> <message>");
        }
    }

    private void handleKickCommand(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length == 2) {
            String userToKick = parts[1];
            if (names.contains(userToKick)) {
                // Notify the kicked user
                for (PrintWriter writer : writers) {
                    if (writer != out) {
                        writer.println("MESSAGE You have been kicked by the coordinator.");
                    }
                }
                // Remove the user from the names and writers sets
                names.remove(userToKick);
                writers.removeIf(writer -> writer == out);
                notifyAllClients("MESSAGE " + userToKick + " has been kicked by the coordinator.");
                ChatServer.broadcastUserList();
            } else {
                out.println("MESSAGE User " + userToKick + " not found.");
            }
        } else {
            out.println("MESSAGE Invalid kick command. Use /kick <username>");
        }
    }

    private void handleMuteCommand(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length == 2) {
            String userToMute = parts[1];
            if (names.contains(userToMute)) {
                mutedUsers.add(userToMute);
                notifyAllClients("MESSAGE " + userToMute + " has been muted by the coordinator.");
            } else {
                out.println("MESSAGE User " + userToMute + " not found.");
            }
        } else {
            out.println("MESSAGE Invalid mute command. Use /mute <username>");
        }
    }

    private void handleUnmuteCommand(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length == 2) {
            String userToUnmute = parts[1];
            if (names.contains(userToUnmute)) {
                mutedUsers.remove(userToUnmute);
                notifyAllClients("MESSAGE " + userToUnmute + " has been unmuted by the coordinator.");
            } else {
                out.println("MESSAGE User " + userToUnmute + " not found.");
            }
        } else {
            out.println("MESSAGE Invalid unmute command. Use /unmute <username>");
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