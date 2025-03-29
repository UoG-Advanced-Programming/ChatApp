package com.example.common.users;

import com.example.common.utils.IDGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The User class represents a user in the system.
 * Each user has a unique ID, username, creation timestamp, and a coordinator status.
 */
public class User {
    private String id; // Unique identifier for the user
    private String username; // Username of the user
    private LocalDateTime createdAt; // Timestamp when the user was created
    private boolean isCoordinator; // Coordinator status of the user

    /**
     * Constructor to create a new user with a username.
     * Generates a unique ID and sets the creation timestamp.
     *
     * @param username The username for the new user
     */
    public User(String username) {
        this.id = IDGenerator.generateUUID(); // Generate a unique ID for the user
        this.username = username; // Set the username
        this.createdAt = LocalDateTime.now(); // Set the creation timestamp to the current time
        this.isCoordinator = false; // Default the coordinator status to false
    }

    // Getters and Setters

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's unique identifier
     */
    public String getId() { return id; }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id The new unique identifier for the user
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the username of the user.
     *
     * @return The user's username
     */
    public String getUsername() { return username; }

    /**
     * Sets the username of the user.
     *
     * @param username The new username for the user
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the creation timestamp of the user.
     *
     * @return The timestamp when the user was created
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the creation timestamp of the user.
     *
     * @param createdAt The new creation timestamp for the user
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Gets the coordinator status of the user.
     *
     * @return True if the user is a coordinator, false otherwise
     */
    public boolean getIsCoordinator() { return isCoordinator; }

    /**
     * Sets the coordinator status of the user.
     *
     * @param isCoordinator The new coordinator status for the user
     */
    public void setIsCoordinator(boolean isCoordinator) { this.isCoordinator = isCoordinator; }

    /**
     * Checks if this user is equal to another object.
     * Users are considered equal if they have the same unique identifier.
     *
     * @param o The object to compare to
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check if the objects are the same instance
        if (o == null || getClass() != o.getClass()) return false; // Check if the object is null or not the same class
        User user = (User) o; // Cast the object to User
        return Objects.equals(id, user.id); // Compare the unique identifiers
    }

    /**
     * Generates a hash code for the user based on the unique identifier.
     *
     * @return The hash code for the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // Generate the hash code using the unique identifier
    }
}