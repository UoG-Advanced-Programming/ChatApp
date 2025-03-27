package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.server.network.ChatServer;
import com.example.server.network.ServerHandler;

import java.io.PrintWriter;

public class ServerUserUpdateMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out, ServerHandler handler) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            server.addClient(userUpdateMessage.getUser(), out, handler);
        } else {
            server.removeClient(userUpdateMessage.getUser());
        }
    }
}
