package com.example.server.network;

import com.example.common.messages.Communication;
import com.example.common.utils.MessageSerializer;
import com.example.server.processing.ServerMessageProcessor;
import com.example.server.processing.ServerMessageProcessorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing input stream: " + e.getMessage());
        }
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error initializing output stream: " + e.getMessage());
        }
    }

    /**
     * Returns the IP address of the connected client.
     * @return String representation of the client's IP address
     */
    public String getClientSocket() {
        if (socket != null && socket.getInetAddress() != null) {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }
        return "Unknown";
    }

    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage());
        } finally {
            try {
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void processMessage(String jsonMessage) {
        Communication message = MessageSerializer.deserialize(jsonMessage);
        ServerMessageProcessor processor = ServerMessageProcessorFactory.getProcessor(message.getType());
        processor.processMessage(message, this.server, out, this);
    }
}