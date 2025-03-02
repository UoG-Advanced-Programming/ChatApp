package com.example.models;

public class SystemMessage extends Communication {
    private String systemContent;

    public SystemMessage(String systemContent) {
        super();
        this.systemContent = systemContent;
    }

    public String getSystemContent() { return systemContent; }
    public void setSystemContent(String systemContent) { this.systemContent = systemContent; }

    @Override
    public CommunicationType getType() {
        return CommunicationType.SYSTEM;
    }
}
