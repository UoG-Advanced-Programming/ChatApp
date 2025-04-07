package com.example.common.chats;

import com.example.common.users.User;

/**
 * The PrivateChat class extends the Chat class and represents a private chat
 * between users. It includes properties to track the chat's active status.
 */
public class PrivateChat extends Chat {
    private boolean active = true; // Indicates whether the chat is active

    /**
     * Checks if the chat is active.
     *
     * @return True if the chat is active, false otherwise
     */
    public boolean isActive() { return active; }

    /**
     * Sets the active status of the chat.
     *
     * @param active The new active status of the chat
     */
    public void setActive(boolean active) { this.active = active; }

    /**
     * Returns the display name of the private chat from the perspective of the specified user.
     * This is typically the username of the other participant.
     *
     * @param currentUser The user for whom the other participant's name is displayed.
     * @return The name of the other participant, or "Unknown" if not found.
     */
    @Override
    public String getDisplayName(User currentUser) {
        return getParticipants().stream()
                .filter(user -> !user.equals(currentUser))
                .findFirst()
                .map(User::getUsername)
                .orElse("Unknown");
    }

    /**
     * Gets the type of the chat.
     *
     * @return PRIVATE
     */
    @Override
    public ChatType getType() { return ChatType.PRIVATE; }
}