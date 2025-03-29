package com.example.client.processing;

import com.example.client.gui.Controller;
import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientSystemMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, Controller controller) {
        if (!(message instanceof SystemMessage systemMessage)) {
            return; // Ensure message is of correct type
        }

        SystemMessageType type = systemMessage.getSystemType();
        String content = systemMessage.getContent();

        switch (type) {
            case ID_TRANSITION:
                controller.getGeneralChat().setId(content);
                break;

            case SERVER_SHUTDOWN:
                controller.handleServerDisconnect();
                break; // No further processing needed

            case IP_TRANSITION:
                JsonObject json = JsonParser.parseString(content).getAsJsonObject();
                String ip = json.get("ip").getAsString();
                String port = json.get("port").getAsString();
                controller.setSocket(ip + ":" + port);
                break;

            case COORDINATOR_ID_TRANSITION:
                if (!content.isEmpty()) {
                    controller.findUserById(content).ifPresent(controller::setCoordinator);
                }
                break;

            case HEARTBEAT:
                controller.recordHeartbeat();
                break;

            default:
                // Handle unknown message types
                break;
        }
    }
}
