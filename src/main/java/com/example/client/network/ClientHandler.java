package com.example.client.network;

import com.example.client.gui.ChatController;
import com.example.client.gui.ChatModel;
import com.example.client.gui.ChatView;
import com.example.client.processing.ClientMessageProcessor;
import com.example.client.processing.ClientMessageProcessorFactory;
import com.example.common.messages.Communication;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private final BufferedReader in;
    private final ChatController controller;

    public ClientHandler(BufferedReader in, ChatClient client, User user) {
        this.in = in;
        ChatModel model = new ChatModel(user);
        ChatView view = new ChatView();
        // Initialize controller
        controller = new ChatController(model, view, client);
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
        processor.processMessage(message, controller);
    }
}
