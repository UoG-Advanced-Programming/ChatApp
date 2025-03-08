package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.chats.Chat;
import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;

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
