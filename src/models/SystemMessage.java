package models;

import java.time.LocalDateTime;

public class SystemMessage extends Communication {
    private String systemContent;

    public SystemMessage(String messageId, String systemContent, LocalDateTime timestamp) {
        super(messageId, timestamp);
        this.systemContent = systemContent;
    }

    public String getSystemContent() { return systemContent; }
    public void setSystemContent(String systemContent) { this.systemContent = systemContent; }

    @Override
    public CommunicationType getType() {
        return CommunicationType.SYSTEM;
    }
}
