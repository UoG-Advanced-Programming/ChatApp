package com.example.client.processing;

import com.example.client.gui.Controller;
import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The ClientSystemMessageProcessor class is responsible for processing
 * system messages on the client side. It extends the ClientMessageProcessor
 * class and handles various types of system messages.
 */
public class ClientSystemMessageProcessor extends ClientMessageProcessor {

    /**
     * Processes a system message and updates the GUI accordingly.
     *
     * @param message    The communication message to process
     * @param controller The controller to update the GUI
     */
    @Override
    public void processMessage(Communication message, Controller controller) {
        if (!(message instanceof SystemMessage systemMessage)) {
            return; // Ensure message is of correct type
        }

        SystemMessageType type = systemMessage.getSystemType(); // Get the type of the system message
        String content = systemMessage.getContent(); // Get the content of the system message

        switch (type) {
            case ID_TRANSITION:
                controller.getGeneralChat().setId(content); // Set the ID of the general chat
                break;

            case SERVER_SHUTDOWN:
                controller.handleServerDisconnect(); // Handle server shutdown
                break; // No further processing needed

            case IP_TRANSITION:
                JsonObject json = JsonParser.parseString(content).getAsJsonObject(); // Parse the JSON content
                String ip = json.get("ip").getAsString(); // Extract the IP address
                String port = json.get("port").getAsString(); // Extract the port
                controller.setSocket(ip + ":" + port); // Set the socket address
                break;

            case COORDINATOR_ID_TRANSITION:
                if (!content.isEmpty()) {
                    controller.findUserById(content).ifPresent(controller::setCoordinator); // Set the coordinator if the content is not empty
                }
                break;

            case HEARTBEAT:
                controller.recordHeartbeat(); // Record a heartbeat
                break;

            default:
                // Handle unknown message types
                break;
        }
    }
}