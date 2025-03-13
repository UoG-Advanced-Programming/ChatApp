package com.example.client.gui;

import com.example.client.network.ChatClient;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientGUI {
    private final JFrame frame;
    private final JTextArea chatDisplay;
    private final JTextField messageField;
    private final JList<Chat> chatList;
    private Chat current_chat;
    private final ChatClient client;
    private final Set<User> active_users = new HashSet<>();
    private final DefaultListModel<Chat> chatListModel;
    private final java.util.Map<Chat, StringBuilder> chatHistories = new java.util.HashMap<>();
    private final User user;

    public ClientGUI(ChatClient client, User user) {
        this.client = client;
        this.user = user;
        frame = new JFrame("Chat Client");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // A window listener to handle client exit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClientExit();
            }
        });

        // Control Panel (Top)
        JPanel controlPanel = getJPanel();

        frame.add(controlPanel, BorderLayout.NORTH);

        // Chat List Panel (Left)
        JPanel chatListPanel = new JPanel(new BorderLayout());
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
        JPanel chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BorderLayout());
        chatDisplay = new JTextArea();
        chatDisplay.setEditable(false);
        chatAreaPanel.add(new JScrollPane(chatDisplay), BorderLayout.CENTER);

        // Message Input Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage(current_chat, messageField));
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        chatAreaPanel.add(messagePanel, BorderLayout.SOUTH);

        frame.add(chatAreaPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add "Start Private Chat" button
        JButton startChatButton = new JButton("Start Private Chat");
        startChatButton.addActionListener(e -> startPrivateChat());
        controlPanel.add(startChatButton);

        // Add "Start Group Chat" button
        JButton startGroupChatButton = new JButton("Start Group Chat");
        startGroupChatButton.addActionListener(e -> startGroupChat());
        controlPanel.add(startGroupChatButton);
        return controlPanel;
    }

    public DefaultListModel<Chat> getChatListModel() {
        return chatListModel;
    }

    private void sendMessage(Chat chat, JTextField messageField) {
        if (chat instanceof PrivateChat && !((PrivateChat) chat).isActive()) {
            JOptionPane.showMessageDialog(frame,
                    "This private chat is no longer active.",
                    "Inactive Chat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String messageText = messageField.getText().trim();
        messageField.setText("");
        if (!messageText.isEmpty()) {
            TextMessage message = new TextMessage(chat, user, messageText);
            client.send(message);
        }
    }

    public void showMessage(TextMessage message) {
        Chat chat = message.getChat();
        chatHistories.putIfAbsent(chat, new StringBuilder());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy HH:mm");
        String formattedTime = message.getTimestamp().format(formatter);
        String formattedMessage = "[" + formattedTime + "] " +
                message.getSender().getUsername() + ": " + message.getContent() + "\n";

        getChatHistory(chat).append(formattedMessage);

        // Only display if it's the currently selected chat
        if (chat.equals(current_chat)) {
            chatDisplay.append(formattedMessage);
        }
    }

    private void startPrivateChat() {
        if (active_users.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No active users to start a chat with.",
                    "Start Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show the user selection dialog
        UserSelectionDialog userDialog = new UserSelectionDialog(frame, active_users, user);
        User selectedUser = userDialog.getSelectedUser();

        // If a user was selected, check if a private chat already exists
        if (selectedUser != null) {
            if (hasPrivateChatWith(selectedUser)) {
                JOptionPane.showMessageDialog(frame,
                        "A private chat with " + selectedUser.getUsername() + " already exists.",
                        "Private Chat Exists", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a new private chat
            PrivateChat chat = new PrivateChat(selectedUser.getUsername());
            chat.addParticipant(selectedUser);
            chat.addParticipant(user);
            chatListModel.addElement(chat); // Add the chat to the list
            switchChat(chat); // Switch to the new chat
        }
    }

    public boolean hasPrivateChatWith(User user) {
        for (int i = 0; i < chatListModel.size(); i++) {
            Chat chat = chatListModel.getElementAt(i);
            if (chat instanceof PrivateChat privateChat) {
                if (privateChat.getParticipants().contains(user) && privateChat.getParticipants().contains(this.user)) {
                    return true; // A private chat already exists between these users
                }
            }
        }
        return false; // No private chat exists between these users
    }

    private void startGroupChat() {
        if (active_users.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No active users to start a group chat with.",
                    "Start Group Chat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show the group chat creation dialog
        GroupChatCreationDialog groupChatDialog = new GroupChatCreationDialog(frame, active_users, user);
        Object[] groupChatDetails = groupChatDialog.getGroupChatDetails();

        // If the user provided valid details, create a new group chat
        if (groupChatDetails != null) {
            String chatName = (String) groupChatDetails[0];
            List<User> selectedUsers = (List<User>) groupChatDetails[1];

            GroupChat chat = new GroupChat(chatName);
            chat.addParticipant(user); // Add the current user to the group chat
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
        if (chat != null && !chat.equals(current_chat)) {
            current_chat = chat;
            chatDisplay.setText(chatHistories.getOrDefault(chat, new StringBuilder()).toString());
        }

        chatList.repaint();
    }

    public boolean hasChat(Chat chat) {
        if (chat instanceof PrivateChat privateChat) {
            for (int i = 0; i < chatListModel.size(); i++) {
                Chat existingChat = chatListModel.getElementAt(i);
                if (existingChat instanceof PrivateChat existingPrivateChat) {
                    if (existingPrivateChat.involvesSameUsers(privateChat)) {
                        return true; // A private chat already exists between these users
                    }
                }
            }
        }
        return chatListModel.contains(chat); // Check for other types of chats
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

            // Iterate through the chat list to find the "General Chat"
            for (int i = 0; i < chatListModel.size(); i++) {
                Chat chat = chatListModel.getElementAt(i);
                if (chat instanceof GroupChat && chat.getName().equals("General Chat")) {
                    // Add the user to the "General Chat"
                    chat.addParticipant(user);

                    // Refresh the chat list UI to reflect the changes
                    chatListModel.set(i, chat); // Update the chat in the list
                    break;
                }
            }
        }
    }

    public void removeActiveUser(User user) {
        active_users.remove(user);
    }

    private StringBuilder getChatHistory(Chat chat) {
        return chatHistories.get(chat);
    }

    private void handleClientExit() {
        // Notify the server that the user is going offline
        UserUpdateMessage userUpdateMessage = new UserUpdateMessage(user, UserStatus.OFFLINE);
        client.send(userUpdateMessage);

        // Close the network connection
        client.disconnect();

        System.out.println("You have left the chat.");
    }

    public void handleUserDeparture(User user) {
        // Remove the user from all group chats
        for (int i = 0; i < chatListModel.size(); i++) {
            Chat chat = chatListModel.getElementAt(i);
            if (chat instanceof GroupChat groupChat) {
                if (groupChat.getParticipants().contains(user)) {
                    groupChat.removeParticipant(user); // Remove the user from the group chat
                    chatListModel.set(i, groupChat); // Update the chat in the list
                }
            } else if (chat instanceof PrivateChat privateChat) {
                if (privateChat.getParticipants().contains(user)) {
                    privateChat.setActive(false); // Mark the private chat as inactive
                    chatListModel.set(i, privateChat); // Update the chat in the list
                }
            }
        }

        // Remove the user from the active_users list
        removeActiveUser(user);
    }
}