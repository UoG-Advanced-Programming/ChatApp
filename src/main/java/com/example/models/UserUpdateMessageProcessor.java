package com.example.models;

import com.example.server.ChatServer;

public class UserUpdateMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());
    }
}
