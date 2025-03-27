package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;
import com.example.server.network.ChatServer;
import com.example.server.network.ServerHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.PrintWriter;
import java.util.Optional;

public class ServerSystemMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out, ServerHandler handler) {
        SystemMessage systemMessage = (SystemMessage) message;

        if (systemMessage.getSystemType().equals(SystemMessageType.IP_REQUEST)) {
            JsonObject json = JsonParser.parseString(systemMessage.getContent()).getAsJsonObject();
            String senderId = json.get("senderId").getAsString();
            String selectedUserId = json.get("selectedUserId").getAsString();
            Optional<User> senderOpt = server.findUserById(senderId);
            Optional<User> selectedUserOpt = server.findUserById(selectedUserId);
            if (selectedUserOpt.isPresent()) {
                User requestedUser = selectedUserOpt.get();
                String ip = server.getUserIpAddress(requestedUser);

                if (ip.equals("Unknown")) {
                    /* @ToDo: Add the logic */
                } else {
                    if (senderOpt.isPresent()) {
                        User sender = senderOpt.get();
                        SystemMessage response = new SystemMessage(SystemMessageType.IP_TRANSITION, ip);
                        server.send(sender, response);
                    }
                }
            }
        }
    }
}
