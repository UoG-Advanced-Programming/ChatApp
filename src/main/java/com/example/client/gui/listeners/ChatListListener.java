package com.example.client.gui.listeners;

import com.example.client.gui.ChatController;
import com.example.common.chats.Chat;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatListListener implements ListSelectionListener {
    private final ChatController controller;

    public ChatListListener(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Chat chat = controller.getView().getChatList().getSelectedValue();

        if (chat != null && !chat.equals(controller.getModel().getCurrentChat())) {
            controller.getModel().setCurrentChat(chat);
            controller.getView().getChatDisplay().setText(controller.getModel().getFormattedChatHistory(chat));
        }
        controller.getView().getChatList().repaint();
    }
}
