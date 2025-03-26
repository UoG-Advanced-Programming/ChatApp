package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.server.network.ChatServer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerUserUpdateMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        User user = userUpdateMessage.getUser();

        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            // Check if there's no coordinator yet
            if (server.getCoordinator() == null) {
                // First user becomes coordinator
                user.setIsCoordinator(true);
                System.out.println("COORDINATOR: " + user.getUsername() + " has been assigned as the coordinator (first user)");
            }

            server.addClient(user, out);
        } else {
            // Store whether this user was the coordinator before removal
            boolean wasCoordinator = (server.getCoordinator() != null &&
                    server.getCoordinator().getId().equals(user.getId()));

            // Remove the user
            server.removeClient(user);

            // If the user was coordinator, reassign a new one
            if (wasCoordinator) {
                reassignCoordinator(server);
            }
        }
    }

    private void reassignCoordinator(ChatServer server) {
        // Get all online users
        List<User> onlineUsers = new ArrayList<>(server.getClientWriters().keySet());

        if (!onlineUsers.isEmpty()) {
            // Choose a random user from the remaining online users
            Random random = new Random();
            User newCoordinator = onlineUsers.get(random.nextInt(onlineUsers.size()));

            // Set the new user as coordinator
            server.setCoordinator(newCoordinator);
            System.out.println("COORDINATOR: " + newCoordinator.getUsername() + " has been randomly assigned as the new coordinator");
        } else {
            System.out.println("COORDINATOR: No users left to assign as coordinator");
        }
    }
}