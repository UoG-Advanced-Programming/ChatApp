package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;

import java.io.PrintWriter;

public abstract class ServerMessageProcessor {
    public abstract void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler);
}
