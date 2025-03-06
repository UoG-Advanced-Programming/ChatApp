package com.example.client;

import com.example.models.*;

import javax.swing.*;
import java.awt.*;
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
    private List<User> active_users = new ArrayList<>(List.of(new User("Arad", "h")));
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

        // Add "Start Private Chat" button
        startChatButton = new JButton("Start Private Chat");
        startChatButton.addActionListener(e -> startPrivateChat());
        controlPanel.add(startChatButton);

        // Add "Start Group Chat" button
        JButton startGroupChatButton = new JButton("Start Group Chat");
        startGroupChatButton.addActionListener(e -> startGroupChat());
        controlPanel.add(startGroupChatButton);

        frame.add(controlPanel, BorderLayout.NORTH);

        // Chat List Panel (Left)
        chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setPreferredSize(new Dimension(150, frame.getHeight()));
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set the custom renderer for the JList
        chatList.setCellRenderer(new ChatListCellRenderer());

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
            TextMessage message = new TextMessage(chat, client.user, messageText);
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

        // Show the user selection dialog
        UserSelectionDialog userDialog = new UserSelectionDialog(frame, active_users);
        User selectedUser = userDialog.getSelectedUser();

        // If a user was selected, create a new private chat
        if (selectedUser != null) {
            PrivateChat chat = new PrivateChat("Private Chat with " + selectedUser.getUsername());
            if (!hasChat(chat)) {
                chatListModel.addElement(chat); // Add the chat to the list
                switchChat(chat); // Switch to the new chat
            }
        }
    }

    private void startGroupChat() {
        if (active_users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active users to start a group chat with.", "Start Group Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show the group chat creation dialog
        GroupChatCreationDialog groupChatDialog = new GroupChatCreationDialog(frame, active_users);
        Object[] groupChatDetails = groupChatDialog.getGroupChatDetails();

        // If the user provided valid details, create a new group chat
        if (groupChatDetails != null) {
            String chatName = (String) groupChatDetails[0];
            List<User> selectedUsers = (List<User>) groupChatDetails[1];

            GroupChat chat = new GroupChat(chatName);
            for (User user : selectedUsers) {
                chat.addParticipant(user); // Add selected users to the chat
            }

            if (!hasChat(chat)) {
                chatListModel.addElement(chat); // Add the chat to the list
                switchChat(chat); // Switch to the new chat
            }
        }
    }

    private void switchChat(Chat chat) {
        current_chat = chat;
        chatDisplay.setText("");
    }

    public boolean hasChat(Chat chat) {
        return chatListModel.contains(chat);
    }

    public void addChat(Chat chat) {
        chatListModel.addElement(chat); // Add to the chat list
    }

    public void updateUsers(List<User> users) {
        active_users = users;
    }
}
