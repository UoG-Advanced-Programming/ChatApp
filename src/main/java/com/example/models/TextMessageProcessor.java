package com.example.models;

import com.example.server.ChatServer;

import java.io.PrintWriter;

public class TextMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        TextMessage textMessage = (TextMessage) message;
        Chat targetChat = textMessage.getChat();

        // Broadcast to active users via the server's clientWriters map
        for (User recipient : targetChat.getParticipants()) {
            PrintWriter writer = server.getClient(recipient);
            if (writer != null) {
                writer.println(MessageSerializer.serialize(textMessage));
            }
        }
    }
}