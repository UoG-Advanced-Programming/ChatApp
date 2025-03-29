package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendButtonListener implements ActionListener {
    private final Controller controller;

    public SendButtonListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Chat currentChat = controller.getModel().getCurrentChat();
        if (currentChat instanceof PrivateChat && !((PrivateChat) currentChat).isActive()) {
            controller.getView().showWarningDialog("This private chat is no longer active.", "Inactive Chat");
            return;
        }

        String messageText = controller.getView().getMessageText();

        if (!messageText.isEmpty()) {
            TextMessage message = new TextMessage(currentChat, controller.getModel().getCurrentUser(), messageText);
            controller.getView().clearMessageField();
            controller.getClient().send(message);
        }
    }
}
