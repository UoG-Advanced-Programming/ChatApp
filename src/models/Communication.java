package models;

import java.time.LocalDateTime;

public abstract class Communication {
    protected String messageId;
    protected LocalDateTime timestamp;

    public Communication(String messageId, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public abstract CommunicationType getType();
}
