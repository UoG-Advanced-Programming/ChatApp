package com.example.client.processing;

import com.example.client.gui.ChatController;
import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;

public class ClientSystemMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatController controller) {
        SystemMessage systemMessage = (SystemMessage) message;
        if (systemMessage.getSystemType().equals(SystemMessageType.ID_TRANSITION)) {
            controller.getGeneralChat().setId(systemMessage.getContent());
        }

        // Special handling for server shutdown message
        if (systemMessage.getSystemType().equals(SystemMessageType.SERVER_SHUTDOWN)) {
            controller.handleServerDisconnect();
            return;
        }

        if (systemMessage.getSystemType().equals(SystemMessageType.IP_TRANSITION)) {
            String ip = systemMessage.getContent();
            controller.setIP(ip);
        }

        if (systemMessage.getSystemType().equals(SystemMessageType.COORDINATOR_ID_TRANSITION)) {
            String id = systemMessage.getContent();

            if (!id.isEmpty()) {
                controller.findUserById(id).ifPresent(controller::setCoordinator);
            }
        }

        if (systemMessage.getSystemType().equals(SystemMessageType.HEARTBEAT)) {
            controller.recordHeartbeat();
        }
    }
}
