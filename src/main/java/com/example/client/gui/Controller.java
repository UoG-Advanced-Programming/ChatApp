package com.example.client.gui;

import com.example.client.network.Client;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.client.gui.listeners.*;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * The Controller class is responsible for handling the interactions between the Model and View,
 * managing the application logic, and responding to user input.
 */
public class Controller {
    private final Model model; // The model containing application state
    private final View view; // The view for the GUI
    private final Client client; // The client for network communication
    private GroupChat generalChat; // The general chat group

    /**
     * Constructs a Controller instance.
     *
     * @param model  The model containing application state
     * @param view   The view for the GUI
     * @param client The client for network communication
     */
    public Controller(Model model, View view, Client client) {
        this.model = model;
        this.view = view;
        this.client = client;
        updateWindowTitle(); // Update the window title with the current user's username

        createGeneralChat(); // Create the general chat

        // Attach listeners to the view components
        this.view.setWindowListener(new WindowListener(this));
        this.view.setPrivateChatButtonListener(new PrivateChatButtonListener(this));
        this.view.setGroupPrivateChatButtonListener(new GroupChatButtonListener(this));
        this.view.setGetHistoryButtonListener(new GetHistoryButtonListener(this));
        this.view.setSendButtonListener(new SendButtonListener(this));
        this.view.setMessageFieldActionListener(new SendButtonListener(this));
        this.view.setChatListListener(new ChatListListener(this));
        this.view.setGetDetailsButtonListener(new GetDetailsButtonListener(this));
    }

    /**
     * Gets the client instance.
     *
     * @return The client instance
     */
    public Client getClient() { return client; }

    /**
     * Gets the model instance.
     *
     * @return The model instance
     */
    public Model getModel() { return model; }

    /**
     * Gets the view instance.
     *
     * @return The view instance
     */
    public View getView() { return view; }

    /**
     * Sets the socket address in the model.
     *
     * @param socket The socket address to set
     */
    public void setSocket(String socket) {
        model.setLastRetrievedSocket(socket); // Store the received IP
    }

    /**
     * Checks if a chat exists in the model.
     *
     * @param chat The chat to check
     * @return True if the chat exists, false otherwise
     */
    public boolean hasChat(Chat chat) {return model.hasChat(chat);}

    /**
     * Adds a chat to the model and view.
     *
     * @param chat The chat to add
     */
    public void addChat(Chat chat) {
        model.addChat(chat);
        view.addChat(chat);
    }

    /**
     * Adds an active user to the model and view.
     *
     * @param user The user to add
     */
    public void addActiveUser(User user) {
        model.addActiveUser(user);
        view.addActiveUser(user);
    }

    /**
     * Removes an active user from the model and view, and updates the chats.
     *
     * @param user The user to remove
     */
    public void removeActiveUser(User user) {
        model.removeActiveUser(user);
        view.removeActiveUser(user);
        for (Chat chat : model.getChats()) {
            if (chat instanceof GroupChat groupChat) {
                if (groupChat.getParticipants().contains(user)) {
                    groupChat.removeParticipant(user); // Remove the user from the group chat
                    view.updateChat(chat); // Update the chat in the UI
                }
            } else if (chat instanceof PrivateChat privateChat) {
                if (privateChat.getParticipants().contains(user)) {
                    privateChat.setActive(false); // Mark the private chat as inactive
                    view.updateChat(chat); // Update the chat in the UI
                }
            }
        }
    }

    /**
     * Displays a message in the chat and updates the model.
     *
     * @param message The message to display
     */
    public void showMessage(TextMessage message) {
        Chat chat = message.getChat();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy HH:mm");
        String formattedTime = message.getTimestamp().format(formatter);
        String formattedMessage = "[" + formattedTime + "] " +
                message.getSender().getUsername() + ": " + message.getContent() + "\n";

        model.addMessageToChat(message);

        // Only display if it's the currently selected chat
        if (chat.equals(model.getCurrentChat())) {
            view.getChatDisplay().append(formattedMessage);
        }
    }

    /**
     * Gets the general chat group.
     *
     * @return The general chat group
     */
    public GroupChat getGeneralChat() {
        return generalChat;
    }

    /**
     * Sets the coordinator user in the model and updates the view.
     *
     * @param user The user to set as coordinator
     */
    public void setCoordinator(User user) {
        model.setCoordinator(user);
        view.getActiveUsersList().repaint();
    }

    /**
     * Gets the coordinator user from the model.
     *
     * @return The coordinator user
     */
    public User getCoordinator() {
        return model.getCoordinator();
    }

    /**
     * Finds a user by their ID in the model's active users.
     *
     * @param userId The ID of the user to find
     * @return An Optional containing the user if found, or an empty Optional if not found
     */
    public Optional<User> findUserById(String userId) {
        // Stream through all connected users and find the one with matching ID
        return model.getActiveUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    /**
     * Records a heartbeat to maintain the connection to the server.
     */
    public void recordHeartbeat() {
        client.recordHeartbeat();
    }

    /**
     * Handles server disconnection by showing an error dialog and exiting the application.
     */
    public void handleServerDisconnect() {
        SwingUtilities.invokeLater(() -> {
            view.showErrorDialog("Server connection lost. Application will now close.", "Server Disconnected");
            // Perform cleanup
            client.disconnect();
            // Exit the application
            System.exit(0);
        });
    }

    /**
     * Updates the window title with the current user's username.
     */
    private void updateWindowTitle() {
        String title = "Client - " + model.getCurrentUser().getUsername();
        view.setWindowTitle(title);
    }

    /**
     * Creates the general chat group and sets it as the current chat.
     */
    private void createGeneralChat() {
        generalChat = new GroupChat("General Chat");
        model.addChat(generalChat);
        model.setCurrentChat(generalChat);
        view.addChat(generalChat);
        // Select the general chat in the chat list
        view.getChatList().setSelectedValue(generalChat, true);
    }
}