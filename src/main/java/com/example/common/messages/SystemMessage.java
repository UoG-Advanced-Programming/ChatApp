package com.example.common.messages;

/**
 * The SystemMessage class extends the Communication class and represents a system message
 * with a specific type and content.
 */
public class SystemMessage extends Communication {
    private final SystemMessageType systemType; // The type of the system message
    private final String content; // The content of the system message

    /**
     * Constructor for creating a new SystemMessage.
     * Initializes the message with the given system type and content.
     *
     * @param systemType The type of the system message
     * @param content    The content of the system message
     */
    public SystemMessage(SystemMessageType systemType, String content) {
        super(CommunicationType.SYSTEM); // Call the parent constructor with the communication type SYSTEM
        this.systemType = systemType; // Set the system message type
        this.content = content; // Set the content of the system message
    }

    // Getters

    /**
     * Gets the type of the system message.
     *
     * @return The type of the system message
     */
    public SystemMessageType getSystemType() {
        return systemType;
    }

    /**
     * Gets the content of the system message.
     *
     * @return The content of the system message
     */
    public String getContent() {
        return content;
    }
}