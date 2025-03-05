package com.example.models;

import com.example.server.ChatServer;

public class SystemMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server) {
        SystemMessage systemMessage = (SystemMessage) message;
        System.out.println("System Notification: " + systemMessage.getSystemContent());
    }
}
