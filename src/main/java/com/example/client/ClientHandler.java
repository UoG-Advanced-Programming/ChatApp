package com.example.client;

import com.example.models.*;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private ClientGUI gui;
    private ChatClient client;

    public ClientHandler(BufferedReader in, ChatClient client) {
        this.in = in;
        this.client = client;
        this.gui = new ClientGUI(this.client);
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
