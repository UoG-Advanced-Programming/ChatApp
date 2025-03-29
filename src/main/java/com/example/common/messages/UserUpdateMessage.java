package com.example.common.messages;

import com.example.common.users.User;

/**
 * The UserUpdateMessage class extends the Communication class and represents a message
 * used to update the status of a user.
 */
public class UserUpdateMessage extends Communication {
    private User user; // The user whose status is being updated
    private UserStatus status; // The new status of the user

    /**
     * Constructor for creating a new UserUpdateMessage.
     * Initializes the message with the given user and status.
     *
     * @param user   The user whose status is being updated
     * @param status The new status of the user
     */
    public UserUpdateMessage(User user, UserStatus status) {
        super(CommunicationType.USER_UPDATE); // Call the parent constructor with the communication type USER_UPDATE
        this.user = user; // Set the user
        this.status = status; // Set the status
    }

    /**
     * Gets the user whose status is being updated.
     *
     * @return The user whose status is being updated
     */
    public User getUser() { return user; }

    /**
     * Sets the user whose status is being updated.
     *
     * @param user The user whose status is being updated
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Gets the new status of the user.
     *
     * @return The new status of the user
     */
    public UserStatus getStatus() { return status; }

    /**
     * Sets the new status of the user.
     *
     * @param status The new status of the user
     */
    public void setStatus(UserStatus status) { this.status = status; }
}