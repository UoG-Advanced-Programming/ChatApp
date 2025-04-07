package com.example.common.chats;

import com.example.common.utils.IDGenerator;
import com.example.common.users.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The Chat class is an abstract base class for different types of chats.
 * It contains common properties and methods for managing chat participants.
 */
public abstract class Chat {
    protected String id; // Unique identifier for the chat
    protected LocalDateTime timestamp; // Timestamp when the chat was created
    protected final Set<User> participants; // Set of participants in the chat

    // Constructor

    /**
     * Constructor for creating a new Chat.
     * Initializes the chat with the given name, generates a unique ID, and sets the timestamp.
     */
    public Chat() {
        this.id = IDGenerator.generateUUID(); // Generate a unique ID for the chat
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        this.participants = new HashSet<>(); // Initialize the set of participants
    }

    // Getters and Setters

    /**
     * Gets the unique identifier of the chat.
     *
     * @return The chat's unique identifier
     */
    public String getId() { return id; }

    /**
     * Sets the unique identifier of the chat.
     *
     * @param id The new unique identifier for the chat
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the timestamp of when the chat was created.
     *
     * @return The timestamp of the chat
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of when the chat was created.
     *
     * @param timestamp The new timestamp for the chat
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Methods for managing participants

    /**
     * Adds a participant to the chat.
     *
     * @param participant The participant to add
     */
    public void addParticipant(User participant) {
        try {
            participants.add(participant);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a participant from the chat.
     *
     * @param participant The participant to remove
     */
    public void removeParticipant(User participant) {
        try {
            participants.remove(participant);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the set of participants in the chat.
     *
     * @return The set of participants
     */
    public Set<User> getParticipants() {
        return participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Abstract methods

    /**
     * Retrieves the display name of the chat from the perspective of the specified user.
     * For group chats, this is the group name. For private chats, it is the name of the other participant.
     *
     * @param currentUser The user for whom the display name should be resolved.
     * @return The display name of the chat.
     */
    public abstract String getDisplayName(User currentUser);

    /**
     * Gets the type of the chat.
     * This method must be implemented by subclasses.
     *
     * @return The type of the chat
     */
    public abstract ChatType getType();
}