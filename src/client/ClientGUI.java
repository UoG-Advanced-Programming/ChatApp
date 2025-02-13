package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Set;
import java.util.HashSet;

public class ClientGUI {
    private static ClientGUI instance;  // Singleton pattern for GUI instance

    private ChatClient client;
    private JFrame frame;
    private JTextField textField;
    private JTextArea messageArea;
    private JButton sendButton, historyButton, activeUsersButton;

    private Set<String> connectedUsers = new HashSet<>();

    // Singleton pattern: Ensures only one instance of GUI
    public static ClientGUI getInstance(ChatClient client) {
        if (instance == null) {
            instance = new ClientGUI(client);
        }
        return instance;
    }

    private ClientGUI(ChatClient client) {
        this.client = client;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Chatter");
        frame.setLayout(new BorderLayout());

        // 📌 Control Panel (Top)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        historyButton = new JButton("History");
        historyButton.addActionListener(e -> loadChatHistory());

        activeUsersButton = new JButton("Active Users");
        activeUsersButton.addActionListener(e -> showActiveUsers());

        controlPanel.add(historyButton);
        controlPanel.add(activeUsersButton);

        // 📌 Chat Area (Center)
        messageArea = new JTextArea(16, 40);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(messageArea);

        // 📌 Input Panel (Bottom)
        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField(40);
        sendButton = new JButton("Send");

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 📌 Frame Layout
        frame.add(controlPanel, BorderLayout.NORTH);  // Control panel at the top
        frame.add(chatScrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 📌 Event Listeners
        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            textField.setText("");
        }
    }

    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String formattedMessage = "[" + java.time.LocalTime.now().withNano(0) + "] " + message;
            messageArea.append(formattedMessage + "\n");

            saveChatMessage(formattedMessage);  // Save chat history
        });
    }

    public void updateUsers(Set<String> users) {
        connectedUsers = users;
    }

    private void showActiveUsers() {
        if (connectedUsers.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active users.", "Active Users", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder userList = new StringBuilder("Active Users:\n");
            for (String user : connectedUsers) {
                userList.append("• ").append(user).append("\n");
            }
            JOptionPane.showMessageDialog(frame, userList.toString(), "Active Users", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveChatMessage(String message) {
        try (FileWriter writer = new FileWriter("chat_history.txt", true)) {
            writer.write(message + "\n");
        } catch (IOException ex) {
            showError("Failed to save chat.");
        }
    }

    private void loadChatHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("chat_history.txt"))) {
            messageArea.setText(""); // Clear current chat
            String line;
            while ((line = reader.readLine()) != null) {
                messageArea.append(line + "\n");
            }
        } catch (IOException ex) {
            showError("Failed to load chat history.");
        }
    }

    public void enableInput() {
        textField.setEditable(true);
    }

    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }
}
