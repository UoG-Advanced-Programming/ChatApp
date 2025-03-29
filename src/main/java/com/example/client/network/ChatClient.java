package com.example.client.network;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class ChatClient {
    private final String host;
    private final int port;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private final ScheduledExecutorService heartbeatChecker = Executors.newScheduledThreadPool(1);
    private volatile long lastHeartbeatTime = System.currentTimeMillis();

    public ChatClient(String host) {
        this.host = host;
        this.port = 7005;
        connectToServer();
        startHeartbeatChecker();
    }

    private void startHeartbeatChecker() {
        heartbeatChecker.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            // If no heartbeat received for more than 20 seconds, assume server is down
            if (currentTime - lastHeartbeatTime > 20000) {
                System.err.println("No heartbeat from server for 20 seconds, assuming server is down");
                disconnect();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null,
                            "Lost connection to server. Application will now close.",
                            "Server Disconnected",
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(0);
                });
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void recordHeartbeat() {
        lastHeartbeatTime = System.currentTimeMillis();
    }

    protected void connectToServer() {  // Protected for testing
        try {
            this.socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
            System.exit(1); // Terminate the program
        }
    }

    public void start() {
        String username = promptForCredentials();
        assert username != null;
        User user = new User(username);
        UserUpdateMessage message = new UserUpdateMessage(user, UserStatus.ONLINE);
        send(message);

        // Register shutdown hook with the current user
        registerShutdownHook(user);

        new Thread(new ClientHandler(in, this, user)).start();
    }

    public void send(Communication message) {
        try {
            assert out != null;
            String jsonMessage = MessageSerializer.serialize(message);
            out.println(jsonMessage);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    protected String promptForCredentials() {  // Protected for testing
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5)); // Neat layout with spacing
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Choose a screen name:");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(label);
        panel.add(usernameField);

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
                    return username;
                } else {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return null; // User canceled
            }
        }
    }

    public void disconnect() {
        heartbeatChecker.shutdown();
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

    private void registerShutdownHook(User currentUser) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered - notifying server about disconnection");
            if (currentUser != null) {
                UserUpdateMessage userUpdateMessage = new UserUpdateMessage(currentUser, UserStatus.OFFLINE);
                send(userUpdateMessage);
                // Small delay to allow message to be sent before full shutdown
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            disconnect();
        }));
    }

    // Getter and setter methods for testing purposes
    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command-line argument");
            System.exit(1); // Exit if no IP is provided
        }

        ChatClient client = new ChatClient(args[0]);

        if (client.socket == null || client.socket.isClosed()) {
            System.err.println("Failed to connect to the server. Exiting...");
            System.exit(1); // Exit if the connection is unsuccessful
        }

        client.start();
    }
}