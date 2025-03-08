package com.example.models;

import java.time.LocalDateTime;

public abstract class Communication {
    protected String messageId;
    protected LocalDateTime timestamp;
    protected CommunicationType type;

    public Communication(CommunicationType type) {
        this.messageId = IDGenerator.generateUUID();
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public CommunicationType getType() { return type; }
}
