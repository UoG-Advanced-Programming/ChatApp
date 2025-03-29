package com.example.server.network;

import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;

/**
 * The CoordinatorManager class is responsible for managing the coordinator among the connected users.
 * It assigns and reassigns the coordinator as needed.
 */
public class CoordinatorManager {
    private final Server server; // Reference to the server
    private User coordinator = null; // The current coordinator

    /**
     * Constructor for CoordinatorManager.
     * Initializes with a reference to the server.
     *
     * @param server The server instance
     */
    public CoordinatorManager(Server server) {
        this.server = server;
    }

    /**
     * Assigns a coordinator if one doesn't exist yet.
     *
     * @param user The user to potentially assign as coordinator
     */
    public void assignCoordinator(User user) {
        // Set the user as coordinator
        setCoordinator(user);

        // Broadcast the coordinator transition message
        SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, getCoordinator().getId());
        server.broadcast(systemMessage);
    }

    /**
     * Reassigns the coordinator when the current one leaves.
     */
    public void reassignCoordinator() {
        // Ask the server to select a random user as coordinator
        User newCoordinator = server.selectRandomUser();

        if (newCoordinator != null) {
            // Set the new user as coordinator
            setCoordinator(newCoordinator);

            // Broadcast the coordinator transition message
            SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, getCoordinator().getId());
            server.broadcast(systemMessage);
        } else {
            // If no new coordinator is found, set coordinator to null
            setCoordinator(null);
        }
    }

    /**
     * Sets a specific user as the coordinator.
     *
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

            // Log the new coordinator assignment
            System.out.println("COORDINATOR: " + coordinator.getUsername() + " is now the coordinator");
        } else {
            // Clear the coordinator if newCoordinator is null
            coordinator = null;
        }
    }

    /**
     * Gets the current coordinator.
     *
     * @return The current coordinator user or null if none exists
     */
    public User getCoordinator() {
        return coordinator;
    }
}