package com.example.common.chats;

import com.example.common.users.User;

/**
 * The PrivateChat class extends the Chat class and represents a private chat
 * between users. It includes properties to track the chat's active status.
 */
public class PrivateChat extends Chat {
    private boolean active = true; // Indicates whether the chat is active

    /**
     * Constructor for creating a new PrivateChat.
     *
     * @param chatName The name of the chat
     */
    public PrivateChat(String chatName) {
        super(chatName); // Call the parent constructor with the chat name
    }

    /**
     * Checks if the chat is active.
     *
     * @return True if the chat is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the chat.
     *
     * @param active The new active status of the chat
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Displays information about the private chat.
     * Overrides the displayChatInfo method in the Chat class.
     */
    @Override
    public void displayChatInfo() {
        System.out.print("Private Chat: " + getName() + " between "); // Print the chat name
        for (User participant : getParticipants()) { // Iterate through the participants
            System.out.print(participant.getUsername() + " "); // Print each participant's username
        }
        System.out.println(); // Print a new line at the end
    }

    /**
     * Gets the type of the chat.
     *
     * @return The type of the chat, which is PRIVATE
     */
    @Override
    public ChatType getType() {
        return ChatType.PRIVATE; // Return the chat type as PRIVATE
    }
}