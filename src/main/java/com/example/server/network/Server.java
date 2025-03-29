package com.example.server.network;

import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Server class is responsible for managing client connections,
 * handling messaging, and coordinating the chat server operations.
 */
public class Server {
    private final Map<User, PrintWriter> clientWriters = new ConcurrentHashMap<>(); // Map to store clients and their output streams
    private final Map<User, ServerHandler> clientHandlers = new ConcurrentHashMap<>(); // Map to store clients and their handlers
    private static final String GENERAL_CHAT_ID = "general-chat"; // Fixed ID for the general chat
    private final CoordinatorManager coordinatorManager; // Manages the coordinator among clients
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Scheduler for periodic tasks
    private final AtomicBoolean isRunning = new AtomicBoolean(true); // Flag to indicate if the server is running

    /**
     * Constructor for the Server class.
     * Initializes the coordinator manager and starts the heartbeat scheduler.
     */
    public Server() {
        // Initialize the coordinator manager with reference to this server
        this.coordinatorManager = new CoordinatorManager(this);

        // Start heartbeat scheduler
        startHeartbeat();
    }

    /**
     * Main method to start the chat server.
     *
     * @param args Command-line arguments
     * @throws Exception if an error occurs while starting the server
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500); // Thread pool for handling client connections
        Server server = new Server();

        // Add shutdown hook to gracefully shut down the server
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        try (ServerSocket listener = new ServerSocket(7005)) { // Create a server socket on port 7005
            while (true) {
                Socket socket = listener.accept(); // Accept incoming client connections
                ServerHandler handler = new ServerHandler(socket, server); // Create a handler for the client
                pool.execute(handler); // Execute the handler in a separate thread
            }
        }
    }

    /**
     * Starts the heartbeat scheduler to send periodic heartbeat messages to clients.
     */
    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning.get()) {
                SystemMessage heartbeat = new SystemMessage(SystemMessageType.HEARTBEAT, ""); // Create a heartbeat message
                broadcast(heartbeat); // Broadcast the heartbeat message to all clients
            }
        }, 0, 10, TimeUnit.SECONDS); // Schedule the task to run every 10 seconds
    }

    /**
     * Shuts down the server and notifies all clients.
     */
    public void shutdown() {
        isRunning.set(false); // Set the running flag to false
        scheduler.shutdown(); // Shut down the scheduler
        System.out.println("Server shutting down...");

        // Send termination signal to all clients
        SystemMessage terminationMessage = new SystemMessage(SystemMessageType.SERVER_SHUTDOWN, null);
        broadcast(terminationMessage);

        // Give clients a moment to process the message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        System.out.println("Server terminated.");
    }

    /**
     * Adds a new client to the server.
     *
     * @param user The user to add
     * @param writer The PrintWriter for sending messages to the client
     * @param handler The handler managing the client connection
     */
    public void addClient(User user, PrintWriter writer, ServerHandler handler) {
        // Notify all clients about the new user
        broadcast(new UserUpdateMessage(user, UserStatus.ONLINE));
        clientWriters.put(user, writer); // Add the user and their writer to the map
        clientHandlers.put(user, handler); // Store the handler
        send(user, new SystemMessage(SystemMessageType.ID_TRANSITION, GENERAL_CHAT_ID)); // Send ID transition message to the new user
        for (User activeUser : clientWriters.keySet()) {
            send(user, new UserUpdateMessage(activeUser, UserStatus.ONLINE)); // Notify the new user about all online users
        }
    }

    /**
     * Removes a client from the server.
     *
     * @param user The user to remove
     */
    public void removeClient(User user) {
        clientWriters.remove(user); // Remove the user from the writers map
        clientHandlers.remove(user); // Remove the user from the handlers map

        // Notify all clients about the user leaving
        broadcast(new UserUpdateMessage(user, UserStatus.OFFLINE));
    }

    /**
     * Finds a user by their ID from the connected clients.
     *
     * @param userId The ID of the user to find
     * @return Optional containing the User if found, or empty if not found
     */
    public Optional<User> findUserById(String userId) {
        // Stream through all connected users and find the one with matching ID
        return clientWriters.keySet().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    /**
     * Returns the socket information of the connected client.
     *
     * @param user The user whose socket information is needed
     * @return String representation of the user's socket address
     */
    public String getUserSocket(User user) {
        ServerHandler handler = clientHandlers.get(user); // Get the handler for the user
        if (handler != null) {
            return handler.getClientSocket(); // Return the socket information
        }
        return "Unknown"; // Return "Unknown" if handler is not found
    }

    /**
     * Retrieves the PrintWriter for a given user.
     *
     * @param user The user whose PrintWriter is needed
     * @return The PrintWriter for the user, or null if not found
     */
    public PrintWriter getClient(User user) {
        return clientWriters.get(user);
    }

    /**
     * Selects a random user from the currently connected clients.
     *
     * @return A randomly selected user, or null if no users are available
     */
    public User selectRandomUser() {
        // Get all online users
        List<User> onlineUsers = new ArrayList<>(clientWriters.keySet());

        if (!onlineUsers.isEmpty()) {
            // Choose a random user from the online users
            Random random = new Random();
            return onlineUsers.get(random.nextInt(onlineUsers.size()));
        } else {
            // Return null without logging - CoordinatorManager will handle messaging
            return null;
        }
    }

    /**
     * Exposes the client writers map for coordinator management.
     *
     * @return The map of client writers
     */
    public Map<User, PrintWriter> getClientWriters() {
        return clientWriters;
    }

    /**
     * Exposes the client handlers map for coordinator management.
     *
     * @return The map of client handlers
     */
    public Map<User, ServerHandler> getClientHandlers() {
        return clientHandlers;
    }

    /**
     * Retrieves the coordinator manager.
     *
     * @return The CoordinatorManager instance
     */
    public CoordinatorManager getCoordinatorManager() {
        return coordinatorManager;
    }

    /**
     * Retrieves the current coordinator.
     *
     * @return The current coordinator user
     */
    public User getCoordinator() {
        return coordinatorManager.getCoordinator();
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message The message to broadcast
     */
    public void broadcast(Communication message) {
        for (PrintWriter writer : clientWriters.values()) {
            writer.println(MessageSerializer.serialize(message)); // Send the serialized message to each client
        }
    }

    /**
     * Sends a message to a specific user.
     *
     * @param user The user to send the message to
     * @param message The message to send
     */
    public void send(User user, Communication message) {
        PrintWriter writer = clientWriters.get(user); // Get the writer for the user
        if (writer != null) {
            writer.println(MessageSerializer.serialize(message)); // Send the serialized message
        }
    }
}