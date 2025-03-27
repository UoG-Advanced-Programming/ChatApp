package com.example.server.processing;

import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.server.network.ChatServer;
import com.example.server.network.CoordinatorManager;

import java.io.PrintWriter;

public class ServerUserUpdateMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatServer server, PrintWriter out) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        User user = userUpdateMessage.getUser();
        CoordinatorManager coordinatorManager = server.getCoordinatorManager();

        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            // First add the client
            server.addClient(user, out);

            // Then try to assign as coordinator if needed
            coordinatorManager.assignCoordinator(user);
        } else {
            // First check if this user is the coordinator
            boolean needReassignment = coordinatorManager.getCoordinator() != null &&
                    coordinatorManager.getCoordinator().getId().equals(user.getId());

            // Remove the client
            server.removeClient(user);

            // Now reassign coordinator if needed
            if (needReassignment) {
                coordinatorManager.reassignCoordinator(user);
            }
        }
    }
}