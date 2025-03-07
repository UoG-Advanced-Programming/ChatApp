package com.example.models;

import com.example.client.ClientGUI;

import javax.swing.*;

public class ClientSystemMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        SystemMessage systemMessage = (SystemMessage) message;
        Chat chat = systemMessage.getChat();
        SwingUtilities.invokeLater(() -> {
            if (!gui.hasChat(chat)) {
                gui.addChat(chat);
            }
        });
    }
}
