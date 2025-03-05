package com.example.models;

import com.example.server.ChatServer;

import java.io.PrintWriter;
import java.util.Set;

public class TextMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server) {
        TextMessage textMessage = (TextMessage) message;
        Chat targetChat = textMessage.getChat();

        // Get all users in the chat
        Set<User> recipients = targetChat.getParticipants();

        // Broadcast to active users via the server's clientWriters map
        for (User recipient : recipients) {
            PrintWriter writer = server.getClient(recipient);
            if (writer != null) {
                String jsonMessage = MessageSerializer.serialize(textMessage);
                writer.println(jsonMessage);
            }
        }
    }
}