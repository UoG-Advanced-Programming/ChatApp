package com.example.server.processing;

import com.example.common.messages.CommunicationType;

/**
 * The ServerMessageProcessorFactory class is responsible for creating instances of ServerMessageProcessor
 * based on the type of communication message received.
 */
public class ServerMessageProcessorFactory {

    /**
     * Returns the appropriate ServerMessageProcessor based on the CommunicationType.
     *
     * @param type The type of communication message
     * @return The corresponding ServerMessageProcessor
     */
    public static ServerMessageProcessor getProcessor(CommunicationType type) {
        // Use a switch expression to return the appropriate processor based on the communication type
        return switch (type) {
            // For TEXT type, return an instance of ServerTextMessageProcessor
            case TEXT -> new ServerTextMessageProcessor();

            // For USER_UPDATE type, return an instance of ServerUserUpdateMessageProcessor
            case USER_UPDATE -> new ServerUserUpdateMessageProcessor();

            // For SYSTEM type, return an instance of ServerSystemMessageProcessor
            case SYSTEM -> new ServerSystemMessageProcessor();
        };
    }
}