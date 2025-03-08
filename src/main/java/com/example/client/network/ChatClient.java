package com.example.client.network;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.GridLayout;

public class ChatClient {
    private final String host;
    private final int port;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String host) {
        this.host = host;
        this.port = 7005;
        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }
    public void start() {
        String[] credentials = promptForCredentials();
        assert credentials != null;
        User user = new User(credentials[0], credentials[1]);
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

    private String[] promptForCredentials() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Choose a screen name:"));
        panel.add(usernameField);
        panel.add(new JLabel("Enter your password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            return new String[] { username, password };
        }
        return null;
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
