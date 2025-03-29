package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The GetHistoryButtonListener class listens for get history button actions and handles
 * the saving of chat history to a file in the chat application.
 */
public class GetHistoryButtonListener implements ActionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a GetHistoryButtonListener instance.
     *
     * @param controller The controller managing the application
     */
    public GetHistoryButtonListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the action performed event when the get history button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Chat currentChat = controller.getModel().getCurrentChat(); // Get the current chat
        // Retrieve chat history
        String chatHistory = controller.getModel().getFormattedChatHistory(currentChat);

        // Specify the file path
        String fileName = currentChat.getName().replace(" ", "_") + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write chat history to the file
            writer.write(chatHistory);
            writer.flush();

            // Notify user of success
            controller.getView().showMessageDialog("Chat history saved to " + fileName, "History Saved");
        } catch (IOException ex) {
            // Handle any file operation errors
            controller.getView().showErrorDialog("Error saving chat history: " + ex.getMessage(), "Error");
        }
    }
}