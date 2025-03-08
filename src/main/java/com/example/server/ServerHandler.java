package com.example.server;

import com.example.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerHandler(Socket socket, ChatServer server) {
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
        MessageProcessor processor = MessageProcessorFactory.getProcessor(message.getType());
        processor.processMessage(message, this.server, out);
    }
}