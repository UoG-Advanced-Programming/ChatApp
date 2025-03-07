package com.example.models;

public class MessageProcessorFactory {
    public static MessageProcessor getProcessor(CommunicationType type) {
        return switch (type) {
            case TEXT -> new TextMessageProcessor();
            case USER_UPDATE -> new UserUpdateMessageProcessor();
            case SYSTEM -> new SystemMessageProcessor();
        };
    }
}
