package com.example.client.processing;

import com.example.client.gui.Controller;
import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;

/**
 * The ClientUserUpdateMessageProcessor class is responsible for processing
 * user update messages on the client side. It extends the ClientMessageProcessor
 * class and handles messages related to user status updates.
 */
public class ClientUserUpdateMessageProcessor extends ClientMessageProcessor {

    /**
     * Processes a user update message and updates the GUI accordingly.
     *
     * @param message    The communication message to process
     * @param controller The controller to update the GUI
     */
    @Override
    public void processMessage(Communication message, Controller controller) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message; // Cast the message to UserUpdateMessage
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus()); // Print user status update

        // Update the list of active users in the GUI based on the user's status
        if (userUpdateMessage.getStatus() == UserStatus.ONLINE) {
            controller.addActiveUser(userUpdateMessage.getUser()); // Add user to active users list if online
        } else if (userUpdateMessage.getStatus() == UserStatus.OFFLINE) {
            controller.removeActiveUser(userUpdateMessage.getUser()); // Remove user from active users list if offline
        }
    }
}