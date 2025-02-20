package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ClientGUI {
    private static ClientGUI instance;
    private ChatClient client;
    private JFrame frame;
    private JTextField textField;
    private JTextArea messageArea;
    private JButton sendButton, historyButton, activeUsersButton, startChatButton;
    private JList<String> chatList;
    private DefaultListModel<String> chatListModel;
    private Map<String, JTextArea> chatWindows;
    private Set<String> connectedUsers = new HashSet<>();
    private String currentChat = "General";

    public static ClientGUI getInstance(ChatClient client) {
        if (instance == null) {
            instance = new ClientGUI(client);
        }
        return instance;
    }

    private ClientGUI(ChatClient client) {
        this.client = client;
        this.chatWindows = new HashMap<>();
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Chatter");
        frame.setLayout(new BorderLayout());

        // Left Panel: Chat List
        JPanel leftPanel = new JPanel(new BorderLayout());
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.addListSelectionListener(e -> switchChat(chatList.getSelectedValue()));
        leftPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);

        startChatButton = new JButton("Start Chat");
        startChatButton.addActionListener(e -> startPrivateChat());
        leftPanel.add(startChatButton, BorderLayout.NORTH);

        // Center Panel: Chat Window
        JPanel centerPanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea(16, 40);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        chatWindows.put("General", messageArea);
        chatListModel.addElement("General");

        // Bottom Panel: Input Field
        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField(40);
        sendButton = new JButton("Send");

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());

        // Right Panel: Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        historyButton = new JButton("History");
        activeUsersButton = new JButton("Active Users");

        historyButton.addActionListener(e -> loadChatHistory());
        activeUsersButton.addActionListener(e -> showActiveUsers());

        controlPanel.add(historyButton);
        controlPanel.add(activeUsersButton);

        // Frame Layout
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void switchChat(String chatName) {
        if (chatName != null && chatWindows.containsKey(chatName)) {
            currentChat = chatName;
            messageArea.setText(chatWindows.get(chatName).getText());
        }
    }

    private void startPrivateChat() {
        if (connectedUsers.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active users to start a chat with.", "Start Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String user = (String) JOptionPane.showInputDialog(frame, "Select user to chat with:", "Start Private Chat",
                JOptionPane.PLAIN_MESSAGE, null, connectedUsers.toArray(), null);

        if (user != null && !chatWindows.containsKey(user)) {
            JTextArea newChatArea = new JTextArea(16, 40);
            newChatArea.setEditable(false);
            chatWindows.put(user, newChatArea);
            chatListModel.addElement(user);
        }

        if (user != null) {
            switchChat(user);  // Switch to the private chat immediately
        }
    }



    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            if (currentChat.equals("General")) {
                client.sendMessage(message);  // Keep General chat format unchanged
            } else {
                client.sendMessage("PRIVATE " + currentChat + " " + message);
            }
            textField.setText("");
        }
    }



    public void showMessage(String chatName, String message) {
        SwingUtilities.invokeLater(() -> {
            String formattedMessage = "[" + java.time.LocalTime.now().withNano(0) + "] " + message;

            chatWindows.computeIfAbsent(chatName, k -> {
                JTextArea newChatArea = new JTextArea(16, 40);
                newChatArea.setEditable(false);
                chatListModel.addElement(chatName);
                return newChatArea;
            });

            chatWindows.get(chatName).append(formattedMessage + "\n");

            saveChatMessage(formattedMessage);
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

    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public void enableInput() {
        textField.setEditable(true);
    }
}
