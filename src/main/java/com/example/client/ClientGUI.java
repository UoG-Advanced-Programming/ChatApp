package com.example.client;

import com.example.models.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<User> active_users = new HashSet<>();
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
        }
    }

    public void showMessage(TextMessage message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy HH:mm"); // Example: "Friday, Mar 07 2025 18:28"
        String formattedTime = message.getTimestamp().format(formatter);

        chatDisplay.append("[" + formattedTime + "] " +
                message.getSender().getUsername() + ": " + message.getContent() + "\n");
    }

    private void startPrivateChat() {
        if (active_users.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No active users to start a chat with.",
                    "Start Chat", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(frame,
                    "No active users to start a group chat with.",
                    "Start Group Chat", JOptionPane.INFORMATION_MESSAGE);
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
        if (!chat.equals(current_chat)) {
            current_chat = chat;
            chatDisplay.setText("");
        }
    }

    public boolean hasChat(Chat chat) {
        return chatListModel.contains(chat);
    }

    public void addChat(Chat chat) {
        chatListModel.addElement(chat);
        if (chat.getName().equals("General Chat")) {
            switchChat(chat);
        }
    }

    public void addActiveUser(User user) {
        if (!active_users.contains(user)) {
            active_users.add(user);
            refreshUserList(); // Refresh the UI if necessary

            // Iterate through the chat list to find the "General Chat"
            for (int i = 0; i < chatListModel.size(); i++) {
                Chat chat = chatListModel.getElementAt(i);
                if (chat instanceof GroupChat && chat.getName().equals("General Chat")) {
                    // Add the user to the "General Chat"
                    ((GroupChat) chat).addParticipant(user);

                    // Refresh the chat list UI to reflect the changes
                    chatListModel.set(i, chat); // Update the chat in the list
                    break;
                }
            }
        }
    }

    public void removeActiveUser(User user) {
        active_users.remove(user);
        refreshUserList(); // Refresh the UI if necessary
    }

    private void refreshUserList() {
        // Refresh any UI components that depend on the list of active users
        // For example, if you have a JList or JComboBox showing active users, update it here
        System.out.println("Active users updated: " + active_users);
    }

}
