package com.example.models;

public class SystemMessage extends Communication {
    private SystemMessageType systemType;
    private Chat chat;

    // Constructor for chat initialization
    public SystemMessage(SystemMessageType systemType, Chat chat) {
        super(CommunicationType.SYSTEM);
        this.systemType = systemType;
        this.chat = chat;
    }

    // Getters
    public SystemMessageType getSystemType() {
        return systemType;
    }

    public Chat getChat() {
        return chat;
    }

    @Override public CommunicationType getType() {
        return CommunicationType.SYSTEM;
    }
}