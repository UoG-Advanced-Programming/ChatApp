package com.example.models;

import com.example.server.ChatServer;

import java.io.PrintWriter;

public class SystemMessageProcessor extends MessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        SystemMessage systemMessage = (SystemMessage) message;
        System.out.println("System Notification: " + systemMessage.getChat());
    }
}
