package com.example.client;

import com.example.models.*;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private final BufferedReader in;
    private final ClientGUI gui;

    public ClientHandler(BufferedReader in, ChatClient client) {
        this.in = in;
        this.gui = new ClientGUI(client);
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage());
        }
    }

    public void processMessage(String jsonMessage) {
        Communication message = MessageSerializer.deserialize(jsonMessage);
        ClientMessageProcessor processor = ClientMessageProcessorFactory.getProcessor(message.getType());
        processor.processMessage(message, gui);
    }
}
