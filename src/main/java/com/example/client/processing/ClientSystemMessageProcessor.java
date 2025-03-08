package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.users.User;

import javax.swing.*;
import java.util.Set;

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

        // If the chat is the General Chat, add its participants to the active_users list
        if (chat instanceof GroupChat && chat.getName().equals("General Chat")) {
            Set<User> participants = chat.getParticipants();
            SwingUtilities.invokeLater(() -> {
                for (User participant : participants) {
                    gui.addActiveUser(participant); // Add each participant to the active_users list
                }
            });
        }
    }
}
