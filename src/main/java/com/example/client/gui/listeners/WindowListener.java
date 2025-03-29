package com.example.client.gui.listeners;

import com.example.client.gui.ChatController;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowListener extends WindowAdapter {
    private final ChatController controller;

    public WindowListener(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // Notify the server that the user is going offline
        UserUpdateMessage userUpdateMessage = new UserUpdateMessage(controller.getModel().getCurrentUser(), UserStatus.OFFLINE);
        controller.getClient().send(userUpdateMessage);

        // Close the network connection
        controller.getClient().disconnect();

        System.out.println("You have left the chat.");
    }
}