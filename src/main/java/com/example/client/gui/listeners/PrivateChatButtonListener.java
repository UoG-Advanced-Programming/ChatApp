package com.example.client.gui.listeners;

import com.example.client.gui.ChatController;
import com.example.common.chats.PrivateChat;
import com.example.common.users.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrivateChatButtonListener implements ActionListener {
    private final ChatController controller;

    public PrivateChatButtonListener(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.getModel().getActiveUsers().isEmpty()) {
            controller.getView().showInfoDialog("No active users to start a chat with.", "Start Chat");
            return;
        }
        // Ask View to show user selection dialog
        User selectedUser = controller.getView().showPrivateChatUserSelectionDialog(controller.getModel().getActiveUsers(),
                controller.getModel().getCurrentUser());

        if (selectedUser != null) {
            if (controller.getModel().hasPrivateChatWith(selectedUser)) {
                controller.getView().showInfoDialog("A private chat with " + selectedUser.getUsername() + " already exists.",
                        "Private Chat Exists");
                return;
            }

            // Create a new private chat
            PrivateChat chat = new PrivateChat(selectedUser.getUsername());
            chat.addParticipant(selectedUser);
            chat.addParticipant(controller.getModel().getCurrentUser());
            controller.getModel().addChat(chat);
            controller.getView().addChat(chat);
            controller.getView().getChatList().setSelectedValue(chat, true);
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat));
            controller.getView().getChatDisplay().setCaretPosition(controller.getView().getChatDisplay().getDocument().getLength()); // Scroll to bottom
        }
    }
}