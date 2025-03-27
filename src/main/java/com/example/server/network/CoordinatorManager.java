package com.example.server.network;

import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CoordinatorManager {
    private final Map<User, PrintWriter> clientWriters;
    private User coordinator = null;

    public CoordinatorManager(Map<User, PrintWriter> clientWriters) {
        this.clientWriters = clientWriters;
    }

    public void assignCoordinator(User user) {
        if (coordinator == null) {
            // First user becomes coordinator
            coordinator = user;
            user.setIsCoordinator(true);
            System.out.println("COORDINATOR: " + user.getUsername() + " has been assigned as the coordinator (first user)");
        }
    }

    public boolean reassignCoordinator(User departingUser) {
        // Check if this was the coordinator
        boolean wasCoordinator = (coordinator != null && coordinator.getId().equals(departingUser.getId()));

        if (wasCoordinator) {
            System.out.println("COORDINATOR: " + departingUser.getUsername() + " (coordinator) has left the chat");
            coordinator = null;

            // Get all online users
            List<User> onlineUsers = new ArrayList<>(clientWriters.keySet());

            if (!onlineUsers.isEmpty()) {
                // Choose a random user from the remaining online users
                Random random = new Random();
                User newCoordinator = onlineUsers.get(random.nextInt(onlineUsers.size()));

                // Set the new user as coordinator
                setCoordinator(newCoordinator);

                System.out.println("COORDINATOR: " + newCoordinator.getUsername() +
                        " has been randomly assigned as the new coordinator");
                return true;
            } else {
                System.out.println("COORDINATOR: No users left to assign as coordinator");
            }
        }

        return false;
    }

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

    public User getCoordinator() {
        return coordinator;
    }
}