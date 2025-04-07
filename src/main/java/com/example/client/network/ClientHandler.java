package com.example.client.network;

import com.example.client.gui.Controller;
import com.example.client.gui.Model;
import com.example.client.gui.View;
import com.example.client.gui.cellRenderers.ChatListCellRenderer;
import com.example.client.processing.ClientMessageProcessor;
import com.example.client.processing.ClientMessageProcessorFactory;
import com.example.common.messages.Communication;
import com.example.common.users.User;
import com.example.common.utils.MessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * The ClientHandler class is responsible for handling incoming messages from the server.
 * It implements the Runnable interface to allow execution in a separate thread.
 */
public class ClientHandler implements Runnable {
    private final BufferedReader in; // BufferedReader for reading messages from the server
    private final Controller controller; // Controller for managing the GUI

    /**
     * Constructor for creating a new ClientHandler.
     *
     * @param in     The BufferedReader for reading messages from the server
     * @param client The client instance
     * @param user   The user instance
     */
    public ClientHandler(BufferedReader in, Client client, User user) {
        this.in = in; // Initialize the BufferedReader
        Model model = new Model(user); // Create a new model with the user
        View view = new View(); // Create a new view
        view.getChatList().setCellRenderer(new ChatListCellRenderer(user)); // Set custom cell renderer
        // Initialize controller
        controller = new Controller(model, view, client); // Create a new controller with the model, view, and client
    }

    /**
     * The run method is the entry point for the thread.
     * It continuously reads messages from the server and processes them.
     */
    @Override
    public void run() {
        String message;
        try {
            // Continuously read messages from the server
            while ((message = in.readLine()) != null) {
                processMessage(message); // Process each message
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage()); // Handle communication errors
        }
    }

    /**
     * Processes a JSON message received from the server.
     *
     * @param jsonMessage The JSON message to process
     */
    public void processMessage(String jsonMessage) {
        Communication message = MessageSerializer.deserialize(jsonMessage); // Deserialize the JSON message to a Communication object
        ClientMessageProcessor processor = ClientMessageProcessorFactory.getProcessor(message.getType()); // Get the appropriate processor based on the message type
        processor.processMessage(message, controller); // Process the message using the processor
    }
}