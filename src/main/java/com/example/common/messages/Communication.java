package com.example.common.messages;

import com.example.common.utils.IDGenerator;

import java.time.LocalDateTime;

/**
 * The Communication class is an abstract base class for different types of messages.
 * It contains common properties such as messageId, timestamp, and type, which are shared
 * by all types of communications.
 */
public abstract class Communication {
    protected String messageId; // Unique identifier for the message
    protected LocalDateTime timestamp; // Timestamp when the message was created
    protected CommunicationType type; // Type of the communication

    /**
     * Constructor for creating a new Communication.
     * Initializes the messageId, timestamp, and type.
     *
     * @param type The type of the communication
     */
    public Communication(CommunicationType type) {
        this.messageId = IDGenerator.generateUUID(); // Generate a unique ID for the message
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        this.type = type; // Set the communication type
    }

    /**
     * Gets the unique identifier of the message.
     *
     * @return The message's unique identifier
     */
    public String getMessageId() { return messageId; }

    /**
     * Sets the unique identifier of the message.
     *
     * @param messageId The new unique identifier for the message
     */
    public void setMessageId(String messageId) { this.messageId = messageId; }

    /**
     * Gets the timestamp of when the message was created.
     *
     * @return The timestamp of the message
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of when the message was created.
     *
     * @param timestamp The new timestamp for the message
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**
     * Gets the type of the communication.
     *
     * @return The type of the communication
     */
    public CommunicationType getType() { return type; }

    /**
     * Sets the type of the communication.
     *
     * @param type The new type of the communication
     */
    public void setType(CommunicationType type) { this.type = type; }
}