package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.server.network.ChatServer;

import java.io.PrintWriter;

public class ServerSystemMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        SystemMessage systemMessage = (SystemMessage) message;
    }
}
