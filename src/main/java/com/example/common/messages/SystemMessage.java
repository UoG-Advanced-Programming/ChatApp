package com.example.common.messages;

public class SystemMessage extends Communication {
    private final SystemMessageType systemType;
    private final String content;

    // Constructor for chat initialization
    public SystemMessage(SystemMessageType systemType, String content) {
        super(CommunicationType.SYSTEM);
        this.systemType = systemType;
        this.content = content;
    }

    // Getters
    public SystemMessageType getSystemType() {
        return systemType;
    }

    public String getContent() {
        return content;
    }
}