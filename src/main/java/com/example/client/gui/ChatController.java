package com.example.client.gui;

import com.example.client.network.ChatClient;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
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
        this.view.setWindowListener(new WindowListener());
        this.view.setPrivateChatButtonListener(new PrivateChatButtonListener());
        this.view.setGroupPrivateChatButtonListener(new GroupChatButtonListener());
        this.view.setSendButtonListener(new SendButtonListener());
        this.view.setMessageFieldActionListener(new SendButtonListener());
        this.view.setChatListListener(new ChatListListener());
        this.view.setGetDetailsButtonListener(new GetDetailsButtonListener());
    }

    public void setIP(String ip) {
        model.setLastRetrievedIP(ip); // Store the received IP
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

    // Inner classes for handling events
    private class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            // Notify the server that the user is going offline
            UserUpdateMessage userUpdateMessage = new UserUpdateMessage(model.getCurrentUser(), UserStatus.OFFLINE);
            client.send(userUpdateMessage);

            // Close the network connection
            client.disconnect();

            System.out.println("You have left the chat.");
        }
    }

    private class PrivateChatButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.getActiveUsers().isEmpty()) {
                view.showInfoDialog("No active users to start a chat with.", "Start Chat");
                return;
            }
            // Ask View to show user selection dialog
            User selectedUser = view.showPrivateChatUserSelectionDialog(model.getActiveUsers(), model.getCurrentUser());

            if (selectedUser != null) {
                if (model.hasPrivateChatWith(selectedUser)) {
                    view.showInfoDialog("A private chat with " + selectedUser.getUsername() + " already exists.",
                            "Private Chat Exists");
                    return;
                }

                // Create a new private chat
                PrivateChat chat = new PrivateChat(selectedUser.getUsername());
                chat.addParticipant(selectedUser);
                chat.addParticipant(model.getCurrentUser());
                model.addChat(chat);
                view.addChat(chat);
                view.getChatList().setSelectedValue(chat, true);
                view.getChatDisplay().setText(model.getFormattedChatHistory(chat));
                view.getChatDisplay().setCaretPosition(view.getChatDisplay().getDocument().getLength()); // Scroll to bottom
            }
        }
    }

    private class GroupChatButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.getActiveUsers().isEmpty()) {
                view.showInfoDialog("No active users to start a chat with.", "Start Group Chat");
                return;
            }
            GroupChatDetails chatDetails = view.showGroupChatUserSelectionDialog(model.getActiveUsers(), model.getCurrentUser());

            if (chatDetails != null) {
                GroupChat chat = new GroupChat(chatDetails.getChatName());
                for (User user : chatDetails.getSelectedUsers()) {
                    chat.addParticipant(user);
                }
                chat.addParticipant(model.getCurrentUser());
                model.addChat(chat);
                model.setCurrentChat(chat);
                view.addChat(chat);
                view.getChatList().setSelectedValue(chat, true);
                view.getChatDisplay().setText(model.getFormattedChatHistory(chat));
                view.getChatDisplay().setCaretPosition(view.getChatDisplay().getDocument().getLength()); // Scroll to bottom
            }

        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Chat currentChat = model.getCurrentChat();
            if (currentChat instanceof PrivateChat && !((PrivateChat) currentChat).isActive()) {
                view.showWarningDialog("This private chat is no longer active.", "Inactive Chat");
                return;
            }

            String messageText = view.getMessageText();

            if (!messageText.isEmpty()) {
                TextMessage message = new TextMessage(currentChat, model.getCurrentUser(), messageText);
                view.clearMessageField();
                client.send(message);
            }
        }
    }

    private class ChatListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            Chat chat = view.getChatList().getSelectedValue();

            if (chat != null && !chat.equals(model.getCurrentChat())) {
                model.setCurrentChat(chat);
                view.getChatDisplay().setText(model.getFormattedChatHistory(chat));
            }
            view.getChatList().repaint();
        }
    }

    private class GetDetailsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            User selectedUser = view.getActiveUsersList().getSelectedValue();
            if (selectedUser != null) {
                model.setLastRetrievedIP(null); // Reset before request
                JsonObject json = new JsonObject();
                json.addProperty("senderId", model.getCurrentUser().getId());
                json.addProperty("selectedUserId", selectedUser.getId());
                SystemMessage request = new SystemMessage(SystemMessageType.IP_REQUEST, json.toString());
                client.send(request);

                // Use SwingWorker to wait for the IP without freezing the UI
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        int timeout = 5000; // 5 seconds timeout
                        int waited = 0;
                        while (model.getLastRetrievedIP() == null && waited < timeout) {
                            Thread.sleep(100); // Wait in small intervals
                            waited += 100;
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (model.getLastRetrievedIP() != null) {
                            view.showMessageDialog(
                                    "Username: " + selectedUser.getUsername() + "\nIP Address: " + model.getLastRetrievedIP(),
                                    "User Details"
                            );
                            model.setCurrentChat(null); // Reset after showing
                        } else {
                            view.showWarningDialog(
                                    "IP retrieval timed out. Please try again.",
                                    "Warning"
                            );
                        }
                    }
                }.execute();
            } else {
                view.showWarningDialog(
                        "Please select a user first!",
                        "Warning"
                );
            }
        }
    }

}
