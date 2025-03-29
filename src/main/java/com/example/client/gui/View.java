package com.example.client.gui;

import com.example.client.gui.cellRenderers.ActiveUserCellRenderer;
import com.example.client.gui.cellRenderers.ChatListCellRenderer;
import com.example.client.gui.dialogs.GroupChatCreationDialog;
import com.example.client.gui.dialogs.PrivateChatCreationDialog;
import com.example.common.chats.Chat;
import com.example.common.users.User;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.Set;

/**
 * The View class is responsible for creating and managing the GUI components
 * for the chat client.
 */
public class View {
    private final JFrame frame; // Main application window
    private final JTextArea chatDisplay; // Area to display chat messages
    private final JTextField messageField; // Field to enter messages
    private final DefaultListModel<Chat> chatListModel; // Model for the list of chats
    private final DefaultListModel<User> activeUsersListModel; // Model for the list of active users
    private final JList<Chat> chatList; // List component for chats
    private final JList<User> activeUsersList; // List component for active users

    // Buttons
    private final JButton startPrivateChatButton; // Button to start a private chat
    private final JButton startGroupChatButton; // Button to start a group chat
    private final JButton getHistoryButton; // Button to get chat history
    private final JButton sendButton; // Button to send a message
    private final JButton getDetailsButton; // Button to get details of a user

    /**
     * Constructor for creating the View.
     * Initializes and sets up the GUI components.
     */
    public View() {
        frame = new JFrame("Chat Client"); // Create main application window
        frame.setSize(800, 500); // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        frame.setLayout(new BorderLayout()); // Set layout manager

        // Control Panel (Top)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout for control panel

        // Add "Start Private Chat" button
        startPrivateChatButton = new JButton("Start Private Chat");
        controlPanel.add(startPrivateChatButton);

        // Add "Start Group Chat" button
        startGroupChatButton = new JButton("Start Group Chat");
        controlPanel.add(startGroupChatButton);

        // Add "Get History" button
        getHistoryButton = new JButton("Get History");
        controlPanel.add(getHistoryButton);

        frame.add(controlPanel, BorderLayout.NORTH); // Add control panel to the top of the frame

        // Chat List Panel (Left)
        JPanel chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setPreferredSize(new Dimension(150, frame.getHeight())); // Set preferred size for chat list panel
        chatListModel = new DefaultListModel<>(); // Initialize chat list model
        chatList = new JList<>(chatListModel); // Create chat list component
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Set selection mode
        chatList.setCellRenderer(new ChatListCellRenderer()); // Set custom cell renderer
        chatListPanel.add(new JScrollPane(chatList), BorderLayout.CENTER); // Add chat list to chat list panel
        frame.add(chatListPanel, BorderLayout.WEST); // Add chat list panel to the left of the frame

        // Chat Area Panel (Center)
        JPanel chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BorderLayout()); // Set layout for chat area panel
        chatDisplay = new JTextArea(); // Create chat display area
        chatDisplay.setEditable(false); // Make chat display area non-editable
        chatAreaPanel.add(new JScrollPane(chatDisplay), BorderLayout.CENTER); // Add chat display area to chat area panel

        // Message Input Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout()); // Set layout for message panel
        messageField = new JTextField(); // Create message input field
        sendButton = new JButton("Send"); // Create send button
        messagePanel.add(messageField, BorderLayout.CENTER); // Add message input field to message panel
        messagePanel.add(sendButton, BorderLayout.EAST); // Add send button to message panel
        chatAreaPanel.add(messagePanel, BorderLayout.SOUTH); // Add message panel to the bottom of chat area panel

        frame.add(chatAreaPanel, BorderLayout.CENTER); // Add chat area panel to the center of the frame

        // Active Users Panel (Right)
        JPanel activeUsersPanel = new JPanel(new BorderLayout());
        activeUsersPanel.setPreferredSize(new Dimension(200, frame.getHeight())); // Set preferred size for active users panel
        activeUsersListModel = new DefaultListModel<>(); // Initialize active users list model
        activeUsersList = new JList<>(activeUsersListModel); // Create active users list component
        activeUsersList.setCellRenderer(new ActiveUserCellRenderer()); // Set custom cell renderer
        getDetailsButton = new JButton("Get Details"); // Create get details button

        activeUsersPanel.add(new JScrollPane(activeUsersList), BorderLayout.CENTER); // Add active users list to active users panel
        activeUsersPanel.setBorder(BorderFactory.createTitledBorder("Active Users")); // Set border title
        activeUsersPanel.add(getDetailsButton, BorderLayout.SOUTH); // Add get details button to the bottom of active users panel
        frame.add(activeUsersPanel, BorderLayout.EAST); // Add active users panel to the right of the frame

        frame.setVisible(true); // Make the frame visible
    }

    // Methods

    /**
     * Clears the message input field.
     */
    public void clearMessageField() {
        messageField.setText("");
    }

    /**
     * Adds a chat to the chat list model.
     *
     * @param chat The chat to add
     */
    public void addChat(Chat chat) {
        chatListModel.addElement(chat);
    }

    /**
     * Adds an active user to the active users list model.
     *
     * @param activeUser The active user to add
     */
    public void addActiveUser(User activeUser) {
        System.out.println("Adding active user: " + activeUser.getUsername()); // Print the username of the active user
        SwingUtilities.invokeLater(() -> activeUsersListModel.addElement(activeUser)); // Add the active user to the model on the event dispatch thread
    }

    /**
     * Updates a chat in the chat list model.
     *
     * @param chat The chat to update
     */
    public void updateChat(Chat chat) {
        for (int i = 0; i < chatListModel.getSize(); i++) {
            if (chatListModel.getElementAt(i).equals(chat)) {  // Find the chat in the UI list
                chatListModel.set(i, chat);  // Update it
                break;  // Exit loop after updating
            }
        }
    }

    /**
     * Removes an active user from the active users list model.
     *
     * @param user The user to remove
     */
    public void removeActiveUser(User user) {
        activeUsersListModel.removeElement(user);
    }

    // Getters and Setters

    /**
     * Gets the main application frame.
     *
     * @return The main application frame
     */
    public JFrame getFrame() {return frame;}

    /**
     * Gets the chat list component.
     *
     * @return The chat list component
     */
    public JList<Chat> getChatList() {return chatList;}

    /**
     * Gets the active users list component.
     *
     * @return The active users list component
     */
    public JList<User> getActiveUsersList() {return activeUsersList;}

    /**
     * Gets the chat display area.
     *
     * @return The chat display area
     */
    public JTextArea getChatDisplay() {return chatDisplay;}

    /**
     * Gets the text from the message input field.
     *
     * @return The text from the message input field
     */
    public String getMessageText() {
        return messageField.getText();
    }

    /**
     * Sets the window title.
     *
     * @param title The title to set
     */
    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }

    // Dialogs

    /**
     * Shows an information dialog.
     *
     * @param message The message to display
     * @param title   The title of the dialog
     */
    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a warning dialog.
     *
     * @param message The message to display
     * @param title   The title of the dialog
     */
    public void showWarningDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows a message dialog.
     *
     * @param message The message to display
     * @param title   The title of the dialog
     */
    public void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error dialog.
     *
     * @param message The message to display
     * @param title   The title of the dialog
     */
    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a dialog to select a user for a private chat.
     *
     * @param activeUsers  The set of active users
     * @param currentUser  The current user
     * @return The selected user for the private chat
     */
    public User showPrivateChatUserSelectionDialog(Set<User> activeUsers, User currentUser) {
        PrivateChatCreationDialog userDialog = new PrivateChatCreationDialog(frame, activeUsers, currentUser);
        return userDialog.getSelectedUser(); // Return the selected user
    }

    /**
     * Shows a dialog to select users for a group chat.
     *
     * @param users        The set of users
     * @param currentUser  The current user
     * @return The details of the group chat or null if canceled
     */
    public GroupChatDetails showGroupChatUserSelectionDialog(Set<User> users, User currentUser) {
        GroupChatCreationDialog dialog = new GroupChatCreationDialog(frame, users, currentUser);
        dialog.showDialog();

        if (dialog.isConfirmed()) {
            return new GroupChatDetails(dialog.getChatName(), dialog.getSelectedUsers());
        }
        return null; // If canceled, return null
    }

    // Action Listeners

    /**
     * Sets the window listener.
     *
     * @param windowListener The window listener to set
     */
    public void setWindowListener(WindowListener windowListener) {
        frame.addWindowListener(windowListener);
    }

    /**
     * Sets the action listener for the "Start Private Chat" button.
     *
     * @param listener The action listener to set
     */
    public void setPrivateChatButtonListener(ActionListener listener) {
        startPrivateChatButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the "Start Group Chat" button.
     *
     * @param listener The action listener to set
     */
    public void setGroupPrivateChatButtonListener(ActionListener listener) {
        startGroupChatButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the "Get History" button.
     *
     * @param listener The action listener to set
     */
    public void setGetHistoryButtonListener(ActionListener listener) {
        getHistoryButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the send button.
     *
     * @param listener The action listener to set
     */
    public void setSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    /**
     * Sets the list selection listener for the chat list.
     *
     * @param listener The list selection listener to set
     */
    public void setChatListListener(ListSelectionListener listener) {
        chatList.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Sets the action listener for the "Get Details" button.
     *
     * @param listener The action listener to set
     */
    public void setGetDetailsButtonListener(ActionListener listener) {
        getDetailsButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the message input field.
     *
     * @param listener The action listener to set
     */
    public void setMessageFieldActionListener(ActionListener listener) {
        messageField.addActionListener(listener);
    }
}