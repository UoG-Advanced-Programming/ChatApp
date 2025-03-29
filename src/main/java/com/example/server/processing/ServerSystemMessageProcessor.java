package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.PrintWriter;
import java.util.Optional;

/**
 * The ServerSystemMessageProcessor class extends ServerMessageProcessor
 * and is responsible for processing system messages on the server.
 */
public class ServerSystemMessageProcessor extends ServerMessageProcessor {

    /**
     * Processes the incoming system message.
     *
     * @param message The communication message received
     * @param server The server handling the message
     * @param out The PrintWriter to send responses
     * @param handler The server handler managing client connections
     */
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
        // Cast the received message to SystemMessage
        SystemMessage systemMessage = (SystemMessage) message;

        // Check if the system message type is IP_REQUEST
        if (systemMessage.getSystemType().equals(SystemMessageType.IP_REQUEST)) {
            // Parse the JSON content of the system message
            JsonObject json = JsonParser.parseString(systemMessage.getContent()).getAsJsonObject();
            String senderId = json.get("senderId").getAsString();
            String selectedUserId = json.get("selectedUserId").getAsString();

            // Find the sender and selected user by their IDs
            Optional<User> senderOpt = server.findUserById(senderId);
            Optional<User> selectedUserOpt = server.findUserById(selectedUserId);

            // If the selected user is present, proceed with processing
            if (selectedUserOpt.isPresent()) {
                User requestedUser = selectedUserOpt.get();

                // Get the socket address of the requested user
                String socket = server.getUserSocket(requestedUser);

                // Check if the socket address is unknown
                if (socket.equals("Unknown")) {
                    /* @ToDo: Add the logic */
                } else {
                    // If the sender is present, prepare and send the response
                    if (senderOpt.isPresent()) {
                        User sender = senderOpt.get();
                        JsonObject responseContent = new JsonObject();
                        responseContent.addProperty("ip", socket.split(":")[0]);
                        responseContent.addProperty("port", socket.split(":")[1]);

                        // Create a new system message for IP transition and send it to the sender
                        SystemMessage response = new SystemMessage(SystemMessageType.IP_TRANSITION, responseContent.toString());
                        server.send(sender, response);
                    }
                }
            }
        }
        // Check if the system message type is HEARTBEAT
        if (systemMessage.getSystemType().equals(SystemMessageType.HEARTBEAT)) {
            // Find the user who has sent the heartbeat
            Optional<User> user = server.findUserById(systemMessage.getContent());

            // Update their records
            server.updateHeartbeat(user);
        }
    }
}