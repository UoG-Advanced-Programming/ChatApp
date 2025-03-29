package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The WindowListener class listens for window events, particularly to handle
 * actions when the window is closing.
 */
public class WindowListener extends WindowAdapter {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a WindowListener instance.
     *
     * @param controller The controller managing the application
     */
    public WindowListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the window closing event.
     *
     * @param e The window event
     */
    @Override
    public void windowClosing(WindowEvent e) {
        // Notify the server that the user is going offline
        UserUpdateMessage userUpdateMessage = new UserUpdateMessage(controller.getModel().getCurrentUser(), UserStatus.OFFLINE);
        controller.getClient().send(userUpdateMessage);

        // Close the network connection
        controller.getClient().disconnect();

        System.out.println("You have left the chat."); // Log message indicating the user has left the chat
    }
}