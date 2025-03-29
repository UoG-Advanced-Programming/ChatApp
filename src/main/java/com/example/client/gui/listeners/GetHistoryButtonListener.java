package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

public class GetHistoryButtonListener implements ActionListener {
    private final Controller controller;

    public GetHistoryButtonListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Chat currentChat = controller.getModel().getCurrentChat();
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