package com.example.models;

import java.awt.*;

public class MessageProcessorFactory {
    public static MessageProcessor getProcessor(CommunicationType type) {
        switch (type) {
            case TEXT:
                return new TextMessageProcessor();
            case USER_UPDATE:
                return new UserUpdateMessageProcessor();
            case SYSTEM:
                return new SystemMessageProcessor();
            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }
}
