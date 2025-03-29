package com.example.server.network;

import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;
import com.example.server.network.ChatServer;

public class CoordinatorManager {
    private final ChatServer server;
    private User coordinator = null;

    public CoordinatorManager(ChatServer chatServer) {
        this.server = chatServer;
    }

    /**
     * Assigns a coordinator if one doesn't exist yet.
     * @param user The user to potentially assign as coordinator
     */
    public void assignCoordinator(User user) {
        setCoordinator(user);
        SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, getCoordinator().getId());
        server.broadcast(systemMessage);
    }

    /**
     * Reassigns the coordinator when the current one leaves
     */
    public void reassignCoordinator() {
        // Ask the server to select a random user as coordinator
        User newCoordinator = server.selectRandomUser();

        if (newCoordinator != null) {
            // Set the new user as coordinator
            setCoordinator(newCoordinator);
            SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, getCoordinator().getId());
            server.broadcast(systemMessage);
        } else {
            setCoordinator(null);
        }

    }

    /**
     * Sets a specific user as the coordinator
     * @param newCoordinator The user to set as coordinator
     */
    public void setCoordinator(User newCoordinator) {
        if (newCoordinator != null) {
            // First, reset any existing coordinator
            if (coordinator != null) {
                coordinator.setIsCoordinator(false);
            }

            // Set the new coordinator
            coordinator = newCoordinator;
            coordinator.setIsCoordinator(true);
            System.out.println("COORDINATOR: " + coordinator.getUsername() + " is now the coordinator");
        } else {
            coordinator = null;
        }
    }

    /**
     * Gets the current coordinator
     * @return The current coordinator user or null if none exists
     */
    public User getCoordinator() {
        return coordinator;
    }
}