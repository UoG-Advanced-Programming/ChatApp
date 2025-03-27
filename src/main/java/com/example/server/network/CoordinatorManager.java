package com.example.server.network;

import com.example.common.users.User;
import com.example.server.network.ChatServer;

public class CoordinatorManager {
    private final ChatServer chatServer;
    private User coordinator = null;

    public CoordinatorManager(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    /**
     * Assigns a coordinator if one doesn't exist yet.
     * @param user The user to potentially assign as coordinator
     */
    public void assignCoordinator(User user) {
        if (coordinator == null) {
            // First user becomes coordinator
            coordinator = user;
            user.setIsCoordinator(true);
            System.out.println("COORDINATOR: " + user.getUsername() + " has been assigned as the coordinator (first user)");
        }
    }

    /**
     * Reassigns the coordinator when the current one leaves
     * @param departingUser The user who is leaving, if they were the coordinator
     * @return True if a reassignment was needed and done
     */
    public void reassignCoordinator(User departingUser) {
        // Check if this was the coordinator
        boolean wasCoordinator = (coordinator != null && coordinator.getId().equals(departingUser.getId()));

        if (wasCoordinator) {
            System.out.println("COORDINATOR: " + departingUser.getUsername() + " (coordinator) has left the chat");
            coordinator = null;

            // Ask the server to select a random user as coordinator
            User newCoordinator = chatServer.selectRandomUser();

            if (newCoordinator != null) {
                // Set the new user as coordinator
                setCoordinator(newCoordinator);
                System.out.println("COORDINATOR: " + newCoordinator.getUsername() +
                        " has been randomly assigned as the new coordinator");
            } else {
                System.out.println("COORDINATOR: No users left to assign as coordinator");
            }
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