package com.example.client.gui;

import com.example.client.network.ChatClient;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import com.google.gson.JsonObject;
import com.example.client.gui.listeners.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ChatController {
    private final ChatModel model;
    private final ChatView view;
    private final ChatClient client;
    private GroupChat generalChat;

    public ChatController(ChatModel model, ChatView view, ChatClient client) {
        this.model = model;
        this.view = view;
        this.client = client;
        updateWindowTitle();

        createGeneralChat();

        // Attach listeners
        this.view.setWindowListener(new WindowListener(this));
        this.view.setPrivateChatButtonListener(new PrivateChatButtonListener(this));
        this.view.setGroupPrivateChatButtonListener(new GroupChatButtonListener(this));
        this.view.setGetHistoryButtonListener(new GetHistoryButtonListener(this));
        this.view.setSendButtonListener(new SendButtonListener(this));
        this.view.setMessageFieldActionListener(new SendButtonListener(this));
        this.view.setChatListListener(new ChatListListener(this));
        this.view.setGetDetailsButtonListener(new GetDetailsButtonListener(this));
    }

    public ChatClient getClient() { return client; }

    public ChatModel getModel() { return model; }

    public ChatView getView() { return view; }

    public void setSocket(String socket) {
        model.setLastRetrievedSocket(socket); // Store the received IP
    }

    public boolean hasChat(Chat chat) {return model.hasChat(chat);}

    public void addChat(Chat chat) {
        model.addChat(chat);
        view.addChat(chat);
    }

    public void addActiveUser(User user) {
        model.addActiveUser(user);
        view.addActiveUser(user);
    }

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

    public GroupChat getGeneralChat() {
        return generalChat;
    }

    public void setCoordinator(User user) {
        model.setCoordinator(user);
        view.getActiveUsersList().repaint();
    }

    public User getCoordinator() {
        return model.getCoordinator();
    }

    public Optional<User> findUserById(String userId) {
        // Stream through all connected users and find the one with matching ID
        return model.getActiveUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    public void recordHeartbeat() {
        client.recordHeartbeat();
    }

    public void handleServerDisconnect() {
        SwingUtilities.invokeLater(() -> {
            view.showErrorDialog("Server connection lost. Application will now close.", "Server Disconnected");
            // Perform cleanup
            client.disconnect();
            // Exit the application
            System.exit(0);
        });
    }

    private void updateWindowTitle() {
        String title = "ChatClient - " + model.getCurrentUser().getUsername();
        view.setWindowTitle(title);
    }

    // Create General Chat
    private void createGeneralChat() {
        generalChat = new GroupChat("General Chat");
        model.addChat(generalChat);
        model.setCurrentChat(generalChat);
        view.addChat(generalChat);
        // Select the general chat in the chat list
        view.getChatList().setSelectedValue(generalChat, true);
    }
}
