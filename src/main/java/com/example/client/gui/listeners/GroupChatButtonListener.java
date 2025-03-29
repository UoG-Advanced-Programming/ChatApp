package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.client.gui.GroupChatDetails;
import com.example.common.chats.GroupChat;
import com.example.common.users.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GroupChatButtonListener class listens for group chat button actions and handles
 * the creation of group chats in the chat application.
 */
public class GroupChatButtonListener implements ActionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a GroupChatButtonListener instance.
     *
     * @param controller The controller managing the application
     */
    public GroupChatButtonListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the action performed event when the group chat button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Check if there are no active users to start a group chat with
        if (controller.getModel().getActiveUsers().isEmpty()) {
            controller.getView().showInfoDialog("No active users to start a chat with.", "Start Group Chat");
            return;
        }
        // Ask View to show group chat user selection dialog
        GroupChatDetails chatDetails = controller.getView().showGroupChatUserSelectionDialog(controller.getModel().getActiveUsers(),
                controller.getModel().getCurrentUser());

        if (chatDetails != null) {
            // Create a new group chat
            GroupChat chat = new GroupChat(chatDetails.chatName());
            for (User user : chatDetails.selectedUsers()) {
                chat.addParticipant(user); // Add each selected user to the group chat
            }
            chat.addParticipant(controller.getModel().getCurrentUser()); // Add the current user to the group chat
            controller.getModel().addChat(chat); // Add the group chat to the model
            controller.getModel().setCurrentChat(chat); // Set the group chat as the current chat
            controller.getView().addChat(chat); // Add the group chat to the view
            controller.getView().getChatList().setSelectedValue(chat, true); // Select the new group chat in the chat list
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat)); // Display the chat history
            controller.getView().getChatDisplay().setCaretPosition(controller.getView().getChatDisplay().getDocument().getLength()); // Scroll to bottom
        }
    }
}