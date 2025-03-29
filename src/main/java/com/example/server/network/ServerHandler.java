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

/**
 * The ServerHandler class implements the Runnable interface and handles client communication.
 * It reads messages from the client, processes them, and sends responses.
 */
public class ServerHandler implements Runnable {

    private final Socket socket; // The socket for client-server communication
    private final Server server; // Reference to the server
    private BufferedReader in; // Reader for input stream from the client
    private PrintWriter out; // Writer for output stream to the client

    /**
     * Constructor for ServerHandler.
     * Initializes the socket and server, and sets up the input and output streams.
     *
     * @param socket The client socket
     * @param server The server instance
     */
    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            // Initialize the input stream from the socket
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing input stream: " + e.getMessage());
        }
        try {
            // Initialize the output stream to the socket
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error initializing output stream: " + e.getMessage());
        }
    }

    /**
     * Returns the IP address of the connected client.
     *
     * @return String representation of the client's IP address and port
     */
    public String getClientSocket() {
        if (socket != null && socket.getInetAddress() != null) {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }
        return "Unknown";
    }

    /**
     * The run method is executed when the thread starts.
     * It continuously reads messages from the client, processes them, and handles exceptions.
     */
    public void run() {
        try {
            String message;
            // Continuously read messages from the client
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage());
        } finally {
            try {
                // Close the input stream and socket when done
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Processes the received message by deserializing it and delegating it to the appropriate processor.
     *
     * @param jsonMessage The JSON formatted message received from the client
     */
    public void processMessage(String jsonMessage) {
        // Deserialize the JSON message to a Communication object
        Communication message = MessageSerializer.deserialize(jsonMessage);

        // Get the appropriate message processor based on the message type
        ServerMessageProcessor processor = ServerMessageProcessorFactory.getProcessor(message.getType());

        // Process the message using the obtained processor
        processor.processMessage(message, this.server, out, this);
    }
}