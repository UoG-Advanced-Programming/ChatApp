package com.example.client.network;

import com.example.client.gui.ClientGUI;
import com.example.client.processing.ClientMessageProcessor;
import com.example.client.processing.ClientMessageProcessorFactory;
import com.example.common.messages.Communication;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private final BufferedReader in;
    private final ClientGUI gui;

    public ClientHandler(BufferedReader in, ChatClient client, User user) {
        this.in = in;
        this.gui = new ClientGUI(client, user);
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
