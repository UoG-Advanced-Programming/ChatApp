package com.example.client.network;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient {
    private final String host;
    private final int port;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ChatClient(String host) {
        this.host = host;
        this.port = 7005;
        connectToServer();
    }

    protected void connectToServer() {  // Protected for testing
        try {
            this.socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    public void start() {
        String username = promptForCredentials();
        assert username != null;
        User user = new User(username);
        UserUpdateMessage message = new UserUpdateMessage(user, UserStatus.ONLINE);
        send(message);

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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        ChatClient client = new ChatClient(args[0]);
        client.start();
    }
}