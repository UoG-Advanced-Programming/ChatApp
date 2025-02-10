package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class ChatServer {

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    // Track the coordinator
    private static String coordinator = null;

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(7005)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    /**
     * The client handler task.
     */
    private static class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        /**
         * Constructs a handler thread, squirreling away the socket.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name until a
         * unique one has been submitted, then acknowledges the name and registers the
         * output stream for the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            // Assign the first client as the coordinator
                            if (coordinator == null) {
                                coordinator = name;
                                System.out.println("Coordinator assigned: " + coordinator);
                            }
                            break;
                        }
                    }
                }

                // Notify the new client of the coordinator
                out.println("COORDINATOR " + coordinator);

                // Notify all clients of the new client joining
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has joined");
                }

                // Add the new client's writer to the set
                writers.add(out);

                // Notify the new client that their name has been accepted
                out.println("NAMEACCEPTED " + name);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    } else if (input.toLowerCase().startsWith("/msg")) {
                        // Handle private messages
                        String[] parts = input.split(" ", 3); // Split into 3 parts: /msg, recipient, message
                        if (parts.length == 3) {
                            String recipient = parts[1];
                            String privateMessage = parts[2];
                            boolean recipientFound = false;
                            for (PrintWriter writer : writers) {
                                if (names.contains(recipient)) {
                                    writer.println("PRIVATE " + name + ": " + privateMessage);
                                    System.out.println("Private message from " + name + " to " + recipient + ": " + privateMessage);
                                    recipientFound = true;
                                }
                            }
                            if (!recipientFound) {
                                out.println("MESSAGE Recipient " + recipient + " not found.");
                            }
                        } else {
                            out.println("MESSAGE Invalid private message format. Use /msg <recipient> <message>");
                        }
                    } else {
                        // Broadcast the message to all clients
                        for (PrintWriter writer : writers) {
                            writer.println("MESSAGE " + name + ": " + input);
                            System.out.println("Broadcast message from " + name + ": " + input);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    names.remove(name);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + " has left");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}