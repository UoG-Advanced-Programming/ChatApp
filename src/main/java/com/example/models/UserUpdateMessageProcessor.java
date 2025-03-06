package com.example.models;

import com.example.server.ChatServer;

import java.io.PrintWriter;

public class UserUpdateMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());
        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            server.addClient(userUpdateMessage.getUser(), out);

        }
    }
}
