package com.example.client.gui.listeners;

import com.example.client.gui.Controller;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GetDetailsButtonListener class listens for the "Get Details" button actions and handles
 * the retrieval of user details in the chat application.
 */
public class GetDetailsButtonListener implements ActionListener {
    private final Controller controller; // The controller managing the application

    /**
     * Constructs a GetDetailsButtonListener instance.
     *
     * @param controller The controller managing the application
     */
    public GetDetailsButtonListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles the action performed event when the "Get Details" button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        User selectedUser = controller.getView().getActiveUsersList().getSelectedValue(); // Get the selected user
        if (selectedUser != null) {
            controller.getModel().setLastRetrievedSocket(null); // Reset before request
            JsonObject json = new JsonObject();
            json.addProperty("senderId", controller.getModel().getCurrentUser().getId());
            json.addProperty("selectedUserId", selectedUser.getId());
            SystemMessage request = new SystemMessage(SystemMessageType.IP_REQUEST, json.toString());
            controller.getClient().send(request); // Send the IP request message

            // Use SwingWorker to wait for the IP without freezing the UI
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int timeout = 5000; // 5 seconds timeout
                    int waited = 0;
                    while (controller.getModel().getLastRetrievedSocket() == null && waited < timeout) {
                        Thread.sleep(100); // Wait in small intervals
                        waited += 100;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    String socket = controller.getModel().getLastRetrievedSocket(); // Get the retrieved socket
                    if (socket != null) {
                        // Show user details in a message dialog
                        controller.getView().showMessageDialog("Username: " + selectedUser.getUsername() +
                                        "\nUser ID: " + selectedUser.getId() +
                                        "\nIs Coordinator: " + selectedUser.getIsCoordinator() +
                                        "\nIP Address: " + socket.split(":")[0] +
                                        "\nPort: " + socket.split(":")[1],
                                "User Details"
                        );
                    } else {
                        // Show warning dialog if IP retrieval timed out
                        controller.getView().showWarningDialog(
                                "IP retrieval timed out. Please try again.",
                                "Warning"
                        );
                    }
                }
            }.execute();
        } else {
            // Show warning dialog if no user is selected
            controller.getView().showWarningDialog(
                    "Please select a user first!",
                    "Warning"
            );
        }
    }
}