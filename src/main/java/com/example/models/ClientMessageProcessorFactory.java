package com.example.models;

public class ClientMessageProcessorFactory {
    public static ClientMessageProcessor getProcessor(CommunicationType type) {
        return switch (type) {
            case TEXT -> new ClientTextMessageProcessor();
            case USER_UPDATE -> new ClientUserUpdateMessageProcessor();
            case SYSTEM -> new ClientSystemMessageProcessor();
        };
    }
}
