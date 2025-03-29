package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;
import com.example.common.utils.MessageSerializer;
import com.example.common.chats.Chat;
import com.example.common.users.User;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;

import java.io.PrintWriter;

public class ServerTextMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
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