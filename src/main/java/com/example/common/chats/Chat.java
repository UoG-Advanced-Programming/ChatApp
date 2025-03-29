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
    protected String name; // Name of the chat
    protected LocalDateTime timestamp; // Timestamp when the chat was created
    protected final Set<User> participants; // Set of participants in the chat

    // Constructor

    /**
     * Constructor for creating a new Chat.
     * Initializes the chat with the given name, generates a unique ID, and sets the timestamp.
     *
     * @param name The name of the chat
     */
    public Chat(String name) {
        this.id = IDGenerator.generateUUID(); // Generate a unique ID for the chat
        this.name = name; // Set the chat name
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
     * Gets the name of the chat.
     *
     * @return The name of the chat
     */
    public String getName() { return name; }

    /**
     * Sets the name of the chat.
     *
     * @param name The new name for the chat
     */
    public void setName(String name) { this.name = name; }

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
        if (participants.add(participant)) {
            System.out.println("Participant " + participant.getUsername() + " added to chat: " + getName());
        } else {
            System.out.println("Participant " + participant.getUsername() + " is already in the chat: " + getName());
        }
    }

    /**
     * Removes a participant from the chat.
     *
     * @param participant The participant to remove
     */
    public void removeParticipant(User participant) {
        if (participants.remove(participant)) {
            System.out.println("Participant " + participant.getUsername() + " removed from chat: " + getName());
        } else {
            System.out.println("Participant " + participant.getUsername() + " is not in the chat: " + getName());
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
     * Displays information about the chat.
     * This method must be implemented by subclasses.
     */
    public abstract void displayChatInfo();

    /**
     * Gets the type of the chat.
     * This method must be implemented by subclasses.
     *
     * @return The type of the chat
     */
    public abstract ChatType getType();

    /**
     * Checks if this private chat involves the same two users as another chat.
     *
     * @param other The other private chat to compare
     * @return True if the chats involve the same two users, false otherwise
     */
    public boolean involvesSameUsers(PrivateChat other) {
        return this.participants.equals(other.getParticipants());
    }
}