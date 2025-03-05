package com.example.models;

import com.example.server.ChatServer;

public abstract class MessageProcessor {
    public abstract void processMessage(Communication message, ChatServer server);
}
