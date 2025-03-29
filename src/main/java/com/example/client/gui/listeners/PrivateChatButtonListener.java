package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.PrivateChat;
import com.example.common.users.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The PrivateChatButtonListener class listens for private chat button actions and handles
 * the creation of private chats in the chat application.
 */
public class PrivateChatButtonListener implements ActionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a PrivateChatButtonListener instance.
     *
     * @param controller The controller managing the application
     */
    public PrivateChatButtonListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the action performed event when the private chat button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Check if there are no active users to start a chat with
        if (controller.getModel().getActiveUsers().isEmpty()) {
            controller.getView().showInfoDialog("No active users to start a chat with.", "Start Chat");
            return;
        }
        // Ask View to show user selection dialog
        User selectedUser = controller.getView().showPrivateChatUserSelectionDialog(controller.getModel().getActiveUsers(),
                controller.getModel().getCurrentUser());

        if (selectedUser != null) {
            // Check if a private chat with the selected user already exists
            if (controller.getModel().hasPrivateChatWith(selectedUser)) {
                controller.getView().showInfoDialog("A private chat with " + selectedUser.getUsername() + " already exists.",
                        "Private Chat Exists");
                return;
            }

            // Create a new private chat
            PrivateChat chat = new PrivateChat(selectedUser.getUsername());
            chat.addParticipant(selectedUser); // Add the selected user to the chat
            chat.addParticipant(controller.getModel().getCurrentUser()); // Add the current user to the chat
            controller.getModel().addChat(chat); // Add the chat to the model
            controller.getView().addChat(chat); // Add the chat to the view
            controller.getView().getChatList().setSelectedValue(chat, true); // Select the new chat in the chat list
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat)); // Display the chat history
            controller.getView().getChatDisplay().setCaretPosition(controller.getView().getChatDisplay().getDocument().getLength()); // Scroll to bottom
        }
    }
}