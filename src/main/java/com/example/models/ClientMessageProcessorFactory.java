package com.example.models;

public class ClientMessageProcessorFactory {
    public static ClientMessageProcessor getProcessor(CommunicationType type) {
        switch (type) {
            case TEXT:
                return new ClientTextMessageProcessor();
            case USER_UPDATE:
                return new ClientUserUpdateMessageProcessor();
            case SYSTEM:
                return new ClientSystemMessageProcessor();
            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }
}
