package com.example.client.gui;

import com.example.common.chats.Chat;
import com.example.common.users.User;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.Set;

public class ChatView {
    private final JFrame frame;
    private final JTextArea chatDisplay;
    private final JTextField messageField;
    private final DefaultListModel<Chat> chatListModel;
    private final DefaultListModel<User> activeUsersListModel;
    private final JList<Chat> chatList;
    private final JList<User> activeUsersList;

    // Buttons
    private final JButton startPrivateChatButton;
    private final JButton startGroupChatButton;
    private final JButton sendButton;
    private final JButton getDetailsButton;

    public ChatView() {
        frame = new JFrame("Chat Client");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Control Panel (Top)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add "Start Private Chat" button
        startPrivateChatButton = new JButton("Start Private Chat");
        controlPanel.add(startPrivateChatButton);

        // Add "Start Group Chat" button
        startGroupChatButton = new JButton("Start Group Chat");
        controlPanel.add(startGroupChatButton);
        frame.add(controlPanel, BorderLayout.NORTH);

        // Chat List Panel (Left)
        JPanel chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setPreferredSize(new Dimension(150, frame.getHeight()));
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.setCellRenderer(new ChatListCellRenderer());
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
        sendButton = new JButton("Send");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        chatAreaPanel.add(messagePanel, BorderLayout.SOUTH);

        frame.add(chatAreaPanel, BorderLayout.CENTER);

        // Active Users Panel (Right)
        JPanel activeUsersPanel = new JPanel(new BorderLayout());
        activeUsersPanel.setPreferredSize(new Dimension(200, frame.getHeight()));
        activeUsersListModel = new DefaultListModel<>();
        activeUsersList = new JList<>(activeUsersListModel);
        activeUsersList.setCellRenderer(new ActiveUserCellRenderer());
        getDetailsButton = new JButton("Get Details");

        activeUsersPanel.add(new JScrollPane(activeUsersList), BorderLayout.CENTER);
        activeUsersPanel.setBorder(BorderFactory.createTitledBorder("Active Users"));
        activeUsersPanel.add(getDetailsButton, BorderLayout.SOUTH);
        frame.add(activeUsersPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    // Methods
    public void clearMessageField() {
        messageField.setText("");
    }

    public void addChat(Chat chat) {
        chatListModel.addElement(chat);
    }

    public void addActiveUser(User activeUser) {
        activeUsersListModel.addElement(activeUser);
    }

    public void updateChat(Chat chat) {
        for (int i = 0; i < chatListModel.getSize(); i++) {
            if (chatListModel.getElementAt(i).equals(chat)) {  // Find the chat in the UI list
                chatListModel.set(i, chat);  // Update it
                break;  // Exit loop after updating
            }
        }
    }

    public void removeActiveUser(User user) {
        activeUsersListModel.removeElement(user);
    }

    // Getters and Setters
    public JFrame getFrame() {return frame;}

    public JList<Chat> getChatList() {return chatList;}

    public JList<User> getActiveUsersList() {return activeUsersList;}

    public JTextArea getChatDisplay() {return chatDisplay;}

    public String getMessageText() {
        return messageField.getText();
    }

    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }

    // Dialogs

    // Information Dialog
    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Warning Dialog
    public void showWarningDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.WARNING_MESSAGE);
    }

    // Message Dialog
    public void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Private Chat User Selection Dialog
    public User showPrivateChatUserSelectionDialog(Set<User> activeUsers, User currentUser) {
        UserSelectionDialog userDialog = new UserSelectionDialog(frame, activeUsers, currentUser);
        return userDialog.getSelectedUser(); // Return the selected user
    }

    // Group Chat User Selection Dialog
    public GroupChatDetails showGroupChatUserSelectionDialog(Set<User> users, User currentUser) {
        GroupChatCreationDialog dialog = new GroupChatCreationDialog(frame, users, currentUser);
        dialog.showDialog();

        if (dialog.isConfirmed()) {
            return new GroupChatDetails(dialog.getChatName(), dialog.getSelectedUsers());
        }
        return null; // If canceled, return null
    }

    // Action Listeners
    public void setWindowListener(WindowListener windowListener) {
        frame.addWindowListener(windowListener);
    }

    public void setPrivateChatButtonListener(ActionListener listener) {
        startPrivateChatButton.addActionListener(listener);
    }

    public void setGroupPrivateChatButtonListener(ActionListener listener) {
        startGroupChatButton.addActionListener(listener);
    }

    public void setSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    public void setChatListListener(ListSelectionListener listener) {
        chatList.getSelectionModel().addListSelectionListener(listener);
    }

    public void setGetDetailsButtonListener(ActionListener listener) {
        getDetailsButton.addActionListener(listener);
    }
}