package com.example.client.network;

import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * The Client class handles the connection to the server, sending and receiving messages,
 * and managing the GUI for user interaction.
 */
public class Client {
    private final String host; // Server host
    private final int port; // Server port
    private PrintWriter out; // Output stream for sending messages
    private BufferedReader in; // Input stream for receiving messages
    private Socket socket; // Socket for communication
    private final ScheduledExecutorService heartbeatChecker = Executors.newScheduledThreadPool(1); // Scheduled executor for heartbeat checking
    private volatile long lastHeartbeatTime = System.currentTimeMillis(); // Last heartbeat time
    User user;  // The current user

    /**
     * Constructor for creating a new Client.
     *
     * @param host The server host
     */
    public Client(String host) {
        this.host = host;
        this.port = 7005; // Default server port
        connectToServer(); // Connect to the server
        startHeartbeatChecker(); // Start the heartbeat checker
    }

    /**
     * Send a heartbeat message to server.
     */
    private void sendHeartbeat() {
        SystemMessage heartbeatMessage = new SystemMessage(SystemMessageType.HEARTBEAT, user.getId());
        send(heartbeatMessage);
    }

    /**
     * Starts the heartbeat checker to monitor server connection and send a heartbeat too.
     */
    private void startHeartbeatChecker() {
        heartbeatChecker.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            // Send heartbeat to the server
            sendHeartbeat();
            // If no heartbeat received for more than 20 seconds, assume server is down
            if (currentTime - lastHeartbeatTime > 20000) {
                System.err.println("No heartbeat from server for 20 seconds, assuming server is down");
                disconnect(); // Disconnect the client
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null,
                            "Lost connection to server. Application will now close.",
                            "Server Disconnected",
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(0); // Exit the application
                });
            }
        }, 5, 5, TimeUnit.SECONDS); // Schedule the task to run every 5 seconds
    }

    /**
     * Records the receipt of a heartbeat from the server.
     */
    public void recordHeartbeat() {
        lastHeartbeatTime = System.currentTimeMillis();
    }

    /**
     * Connects to the server.
     * Protected for testing purposes.
     */
    protected void connectToServer() {
        try {
            this.socket = new Socket(host, port); // Create a new socket
            out = new PrintWriter(socket.getOutputStream(), true); // Initialize the output stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Initialize the input stream
            System.out.println("Connected to the server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
            System.exit(1); // Terminate the program if connection fails
        }
    }

    /**
     * Starts the client and handles user login.
     */
    public void start() {
        String username = promptForCredentials(); // Prompt for user credentials
        assert username != null; // Ensure username is not null
        user = new User(username); // Create a new user
        UserUpdateMessage message = new UserUpdateMessage(user, UserStatus.ONLINE); // Create a user update message
        send(message); // Send the user update message

        // Register shutdown hook with the current user
        registerShutdownHook(user);

        new Thread(new ClientHandler(in, this, user)).start(); // Start a new thread to handle incoming messages
    }

    /**
     * Sends a communication message to the server.
     *
     * @param message The communication message to send
     */
    public void send(Communication message) {
        try {
            assert out != null; // Ensure the output stream is not null
            String jsonMessage = MessageSerializer.serialize(message); // Serialize the message to JSON
            out.println(jsonMessage); // Send the JSON message
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for credentials.
     * Protected for testing purposes.
     *
     * @return The entered username or null if canceled
     */
    protected String promptForCredentials() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5)); // Neat layout with spacing
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Set border for the panel

        JLabel label = new JLabel("Choose a screen name:");
        label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Set font for the label

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Set font for the text field

        panel.add(label); // Add label to the panel
        panel.add(usernameField); // Add text field to the panel

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    return username; // Return the entered username
                } else {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return null; // User canceled
            }
        }
    }

    /**
     * Disconnects the client from the server.
     */
    public void disconnect() {
        heartbeatChecker.shutdown(); // Shutdown the heartbeat checker
        try {
            if (socket != null) {
                socket.close(); // Close the socket
            }
            if (out != null) {
                out.close(); // Close the output stream
            }
            if (in != null) {
                in.close(); // Close the input stream
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    /**
     * Registers a shutdown hook to notify the server of disconnection.
     *
     * @param currentUser The current user
     */
    private void registerShutdownHook(User currentUser) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered - notifying server about disconnection");
            if (currentUser != null) {
                UserUpdateMessage userUpdateMessage = new UserUpdateMessage(currentUser, UserStatus.OFFLINE); // Create a user update message
                send(userUpdateMessage); // Send the user update message
                // Small delay to allow message to be sent before full shutdown
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            disconnect(); // Disconnect the client
        }));
    }

    // Getter and setter methods for testing purposes

    /**
     * Gets the output stream.
     *
     * @return The output stream
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * Sets the output stream.
     *
     * @param out The output stream to set
     */
    public void setOut(PrintWriter out) {
        this.out = out;
    }

    /**
     * Gets the input stream.
     *
     * @return The input stream
     */
    public BufferedReader getIn() {
        return in;
    }

    /**
     * Sets the input stream.
     *
     * @param in The input stream to set
     */
    public void setIn(BufferedReader in) {
        this.in = in;
    }

    /**
     * Gets the socket.
     *
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket.
     *
     * @param socket The socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * The main method to start the client application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command-line argument");
            System.exit(1); // Exit if no IP is provided
        }

        Client client = new Client(args[0]); // Create a new client with the server IP

        if (client.socket == null || client.socket.isClosed()) {
            System.err.println("Failed to connect to the server. Exiting...");
            System.exit(1); // Exit if the connection is unsuccessful
        }

        client.start(); // Start the client
    }
}