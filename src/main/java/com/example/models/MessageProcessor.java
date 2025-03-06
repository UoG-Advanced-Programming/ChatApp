package com.example.models;

import com.example.server.ChatServer;

import java.io.PrintWriter;

public abstract class MessageProcessor {
    public abstract void processMessage(Communication message, ChatServer server, PrintWriter out);
}
