package com.example.common.messages;

/**
 * Enum representing different types of system messages.
 */
public enum SystemMessageType {
    ID_TRANSITION,            // Message type for ID transition
    IP_TRANSITION,            // Message type for IP transition
    IP_REQUEST,               // Message type for IP request
    COORDINATOR_ID_TRANSITION,// Message type for coordinator ID transition
    SERVER_SHUTDOWN,          // Message type for server shutdown
    HEARTBEAT,                // Message type for heartbeat
}