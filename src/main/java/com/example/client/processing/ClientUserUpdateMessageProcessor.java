package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.messages.Communication;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;

public class ClientUserUpdateMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        // Update the list of active users in the GUI
        if (userUpdateMessage.getStatus() == UserStatus.ONLINE) {
            gui.addActiveUser(userUpdateMessage.getUser());
        } else if (userUpdateMessage.getStatus() == UserStatus.OFFLINE) {
            gui.handleUserDeparture(userUpdateMessage.getUser());
        }
    }
}