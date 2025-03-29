package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;

import java.io.PrintWriter;

/**
 * The ServerMessageProcessor class is an abstract class that defines
 * the structure for processing messages on the server.
 */
public abstract class ServerMessageProcessor {

    /**
     * Abstract method to process the incoming communication message.
     *
     * @param message The communication message received
     * @param server The server handling the message
     * @param out The PrintWriter to send responses
     * @param handler The server handler managing client connections
     */
    public abstract void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler);
}