package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The SendButtonListener class listens for send button actions and handles
 * the sending of messages in the chat application.
 */
public class SendButtonListener implements ActionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a SendButtonListener instance.
     *
     * @param controller The controller managing the application
     */
    public SendButtonListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the action performed event when the send button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Chat currentChat = controller.getModel().getCurrentChat(); // Get the current chat
        if (currentChat instanceof PrivateChat && !((PrivateChat) currentChat).isActive()) {
            // Show warning dialog if the private chat is no longer active
            controller.getView().showWarningDialog("This private chat is no longer active.", "Inactive Chat");
            return;
        }

        String messageText = controller.getView().getMessageText(); // Get the message text

        if (!messageText.isEmpty()) {
            TextMessage message = new TextMessage(currentChat, controller.getModel().getCurrentUser(), messageText); // Create a new text message
            controller.getView().clearMessageField(); // Clear the message input field
            controller.getClient().send(message); // Send the message to the server
        }
    }
}