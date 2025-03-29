package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.client.gui.GroupChatDetails;
import com.example.common.chats.GroupChat;
import com.example.common.users.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroupChatButtonListener implements ActionListener {
    private final Controller controller;

    public GroupChatButtonListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.getModel().getActiveUsers().isEmpty()) {
            controller.getView().showInfoDialog("No active users to start a chat with.", "Start Group Chat");
            return;
        }
        GroupChatDetails chatDetails = controller.getView().showGroupChatUserSelectionDialog(controller.getModel().getActiveUsers(),
                controller.getModel().getCurrentUser());

        if (chatDetails != null) {
            GroupChat chat = new GroupChat(chatDetails.chatName());
            for (User user : chatDetails.selectedUsers()) {
                chat.addParticipant(user);
            }
            chat.addParticipant(controller.getModel().getCurrentUser());
            controller.getModel().addChat(chat);
            controller.getModel().setCurrentChat(chat);
            controller.getView().addChat(chat);
            controller.getView().getChatList().setSelectedValue(chat, true);
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat));
            controller.getView().getChatDisplay().setCaretPosition(controller.getView().getChatDisplay().getDocument().getLength()); // Scroll to bottom
        }
    }
}