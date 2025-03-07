package com.example.models;

import com.example.client.ClientGUI;

public class ClientUserUpdateMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
        System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());

        // Update the list of active users in the GUI
        if (userUpdateMessage.getStatus() == UserStatus.ONLINE) {
            gui.addActiveUser(userUpdateMessage.getUser());
        } else if (userUpdateMessage.getStatus() == UserStatus.OFFLINE) {
            gui.removeActiveUser(userUpdateMessage.getUser());
        }
    }
}