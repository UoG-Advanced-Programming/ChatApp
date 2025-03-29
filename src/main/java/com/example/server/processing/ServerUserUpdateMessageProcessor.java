package com.example.server.processing;

import com.example.common.messages.*;
import com.example.common.users.User;
import com.example.server.network.Server;
import com.example.server.network.ServerHandler;
import com.example.server.network.CoordinatorManager;

import java.io.PrintWriter;

public class ServerUserUpdateMessageProcessor extends ServerMessageProcessor {
    @Override
    public void processMessage(Communication message, Server server, PrintWriter out, ServerHandler handler) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        User user = userUpdateMessage.getUser();
        CoordinatorManager coordinatorManager = server.getCoordinatorManager();

        if (userUpdateMessage.getStatus().equals(UserStatus.ONLINE)) {
            server.addClient(user, out, handler);

            if (coordinatorManager.getCoordinator() == null) {
                coordinatorManager.assignCoordinator(user);
            }
            SystemMessage systemMessage = new SystemMessage(SystemMessageType.COORDINATOR_ID_TRANSITION, coordinatorManager.getCoordinator().getId());
            server.broadcast(systemMessage);
        } else {
            // First check if this user is the coordinator
            boolean needReassignment = coordinatorManager.getCoordinator() != null &&
                    coordinatorManager.getCoordinator().getId().equals(user.getId());

            // Remove the client
            server.removeClient(user);

            // Now reassign coordinator if needed
            if (needReassignment) {
                System.out.println("COORDINATOR: " + user.getUsername() + " (coordinator) has left the chat");
                coordinatorManager.reassignCoordinator();
            }
        }
    }
}