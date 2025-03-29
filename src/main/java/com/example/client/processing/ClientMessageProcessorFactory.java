package com.example.client.processing;

import com.example.common.messages.CommunicationType;

/**
 * The ClientMessageProcessorFactory class is responsible for creating instances
 * of ClientMessageProcessor based on the type of communication.
 */
public class ClientMessageProcessorFactory {

    /**
     * Returns an instance of ClientMessageProcessor based on the provided communication type.
     *
     * @param type The type of communication
     * @return An instance of ClientMessageProcessor appropriate for the communication type
     */
    public static ClientMessageProcessor getProcessor(CommunicationType type) {
        return switch (type) {
            case TEXT -> new ClientTextMessageProcessor(); // Return a processor for text messages
            case USER_UPDATE -> new ClientUserUpdateMessageProcessor(); // Return a processor for user update messages
            case SYSTEM -> new ClientSystemMessageProcessor(); // Return a processor for system messages
        };
    }
}