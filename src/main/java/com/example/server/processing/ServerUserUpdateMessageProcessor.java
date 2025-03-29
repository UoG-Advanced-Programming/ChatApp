package com.example.server.processing;

import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;
import com.example.server.network.CoordinatorManager;

import java.io.PrintWriter;

/**
 * The ServerUserUpdateMessageProcessor class extends ServerMessageProcessor
 * and is responsible for processing user update messages on the server.
 */
public class ServerUserUpdateMessageProcessor extends ServerMessageProcessor {

    /**
     * Processes the incoming user update message.
     *
     * @param message The communication message received
     * @param server The server handling the message
     * @param out The PrintWriter to send responses
     * @param handler The server handler managing client connections
     */
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
        // Cast the received message to UserUpdateMessage
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;

        // Log the user status update to the console
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        // Retrieve the user from the message
        User user = userUpdateMessage.getUser();

        // Get the CoordinatorManager from the server
        CoordinatorManager coordinatorManager = server.getCoordinatorManager();

        // Check if the user status is ONLINE
        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            // Add the client to the server
            server.addClient(user, out, handler);

            // If there is no current coordinator, assign this user as the coordinator
            if (coordinatorManager.getCoordinator() == null) {
                coordinatorManager.assignCoordinator(user);
            }

            // Create a system message for coordinator transition and broadcast it
            SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, coordinatorManager.getCoordinator().getId());
            server.broadcast(systemMessage);
        } else {
            // Check if the user is the current coordinator and needs reassignment
            boolean needReassignment = coordinatorManager.getCoordinator() != null &&
                    coordinatorManager.getCoordinator().getId().equals(user.getId());

            // Remove the client from the server
            server.removeClient(user);

            // Reassign the coordinator if necessary
            if (needReassignment) {
                System.out.println("COORDINATOR: " + user.getUsername() + " (coordinator) has left the chat");
                coordinatorManager.reassignCoordinator();
            }
        }
    }
}