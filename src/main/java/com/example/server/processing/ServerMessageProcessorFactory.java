package com.example.server.processing;

import com.example.common.messages.CommunicationType;

public class ServerMessageProcessorFactory {
    public static ServerMessageProcessor getProcessor(CommunicationType type) {
        return switch (type) {
            case TEXT -> new ServerTextMessageProcessor();
            case USER_UPDATE -> new ServerUserUpdateMessageProcessor();
            case SYSTEM -> new ServerSystemMessageProcessor();
        };
    }
}
