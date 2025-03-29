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

public class ServerSystemMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
        SystemMessage systemMessage = (SystemMessage) message;

        if (systemMessage.getSystemType().equals(SystemMessageType.IP_REQUEST)) {
            JsonObject json = JsonParser.parseString(systemMessage.getContent()).getAsJsonObject();
            String senderId = json.get("senderId").getAsString();
            String selectedUserId = json.get("selectedUserId").getAsString();
            Optional<User> senderOpt = server.findUserById(senderId);
            Optional<User> selectedUserOpt = server.findUserById(selectedUserId);
            if (selectedUserOpt.isPresent()) {
                User requestedUser = selectedUserOpt.get();
                String socket = server.getUserSocket(requestedUser);

                if (socket.equals("Unknown")) {
                    /* @ToDo: Add the logic */
                } else {
                    if (senderOpt.isPresent()) {
                        User sender = senderOpt.get();
                        JsonObject responseContent = new JsonObject();
                        responseContent.addProperty("ip", socket.split(":")[0]);
                        responseContent.addProperty("port", socket.split(":")[1]);

                        SystemMessage response = new SystemMessage(SystemMessageType.IP_TRANSITION, responseContent.toString());
                        server.send(sender, response);
                    }
                }
            }
        }
    }
}
