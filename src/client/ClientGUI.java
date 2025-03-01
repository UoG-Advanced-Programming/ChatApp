package client;

import models.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI {
    private JFrame frame;
    private JPanel controlPanel;
    private JPanel chatListPanel;
    private JPanel chatAreaPanel;
    private JTextArea chatDisplay;
    private JTextField messageField;
    private JButton sendButton, startChatButton;
    private JList<Chat> chatList;
    private Chat current_chat;
    private ChatClient client;
    private List<User> active_users = new ArrayList<>(List.of(new User("1", "Arad", "h")));
    private DefaultListModel<Chat> chatListModel;

    public ClientGUI(ChatClient client) {
        this.client = client;
        frame = new JFrame("Chat Client");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Control Panel (Top)
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        startChatButton = new JButton("Start Chat");
        startChatButton.addActionListener(e -> startPrivateChat());
        controlPanel.add(startChatButton);
        frame.add(controlPanel, BorderLayout.NORTH);

        // Chat List Panel (Left)
        chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setPreferredSize(new Dimension(150, frame.getHeight()));
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.addListSelectionListener(e -> switchChat(chatList.getSelectedValue()));
        chatListPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
        frame.add(chatListPanel, BorderLayout.WEST);

        // Chat Area Panel (Center)
        chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BorderLayout());
        chatDisplay = new JTextArea();
        chatDisplay.setEditable(false);
        chatAreaPanel.add(new JScrollPane(chatDisplay), BorderLayout.CENTER);

        // Message Input Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage(current_chat, chatDisplay, messageField));
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        chatAreaPanel.add(messagePanel, BorderLayout.SOUTH);

        frame.add(chatAreaPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void sendMessage(Chat chat, JTextArea chatDisplay, JTextField messageField) {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            TextMessage message = new TextMessage(IDGenerator.generateUUID(), chat, client.user, messageText, LocalDateTime.now());
            client.send(message);
            chatDisplay.append("Me: " + messageText + "\n");
            messageField.setText("");
        }
    }

    private void showMessage(PrivateChat chat, String sender, String message) {
        chatDisplay.append(sender + ": " + message + "\n");
    }

    private void startPrivateChat() {
        if (active_users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active users to start a chat with.", "Start Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        User user = (User) JOptionPane.showInputDialog(frame, "Select user to chat with:", "Start Private Chat",
                JOptionPane.PLAIN_MESSAGE, null, active_users.toArray(), null);

        // and if the chat with that user didn't already exist
        if (user != null) {
            PrivateChat chat = new PrivateChat(IDGenerator.generateUUID(), "Private Chat", client.user, user);
            JTextArea newChatArea = new JTextArea(16, 40);
            newChatArea.setEditable(false);
            chatListModel.addElement(chat);
            switchChat(chat);
        }
    }

    private void switchChat(Chat chat) {
        current_chat = chat;
        chatDisplay.setText("");
    }

    public void updateUsers(List<User> users) {
        active_users = users;
    }
}
