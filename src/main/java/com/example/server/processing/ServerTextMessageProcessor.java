package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;
import com.example.common.utils.MessageSerializer;
import com.example.common.chats.Chat;
import com.example.common.users.User;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;

import java.io.PrintWriter;

/**
 * The ServerTextMessageProcessor class extends ServerMessageProcessor
 * and is responsible for processing text messages on the server.
 */
public class ServerTextMessageProcessor extends ServerMessageProcessor {

    /**
     * Processes the incoming text message.
     *
     * @param message The communication message received
     * @param server The server handling the message
     * @param out The PrintWriter to send responses
     * @param handler The server handler managing client connections
     */
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
        // Cast the received message to TextMessage
        TextMessage textMessage = (TextMessage) message;

        // Retrieve the chat from the message
        Chat targetChat = textMessage.getChat();

        // Broadcast the text message to all active users in the chat
        for (User recipient : targetChat.getParticipants()) {
            // Get the PrintWriter for each participant
            PrintWriter writer = server.getClient(recipient);

            // If the writer is not null, send the serialized text message
            if (writer != null) {
                writer.println(MessageSerializer.serialize(textMessage));
            }
        }
    }
}