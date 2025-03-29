package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The ChatListListener class listens for selection changes in the chat list
 * and handles updating the chat display accordingly.
 */
public class ChatListListener implements ListSelectionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a ChatListListener instance.
     *
     * @param controller The controller managing the application
     */
    public ChatListListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the value changed event when the selection in the chat list changes.
     *
     * @param e The list selection event
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        Chat chat = controller.getView().getChatList().getSelectedValue(); // Get the selected chat

        // Check if the selected chat is not null and is different from the current chat
        if (chat != null && !chat.equals(controller.getModel().getCurrentChat())) {
            controller.getModel().setCurrentChat(chat); // Update the current chat in the model
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat)); // Update the chat display with the chat history
        }
        controller.getView().getChatList().repaint(); // Repaint the chat list to reflect any changes
    }
}