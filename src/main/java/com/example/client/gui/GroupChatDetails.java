package com.example.client.gui;

import com.example.common.users.User;
import java.util.List;

/**
 * Represents the details of a group chat, including its name and selected users.
 */
public class GroupChatDetails {
    private final String chatName;
    private final List<User> selectedUsers;

    /**
     * Constructs a GroupChatDetails instance.
     *
     * @param chatName      The name of the group chat (cannot be null or empty).
     * @param selectedUsers The list of users in the group (cannot be null).
     * @throws IllegalArgumentException if chatName is empty or selectedUsers is null.
     */
    public GroupChatDetails(String chatName, List<User> selectedUsers) {
        if (chatName == null || chatName.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat name cannot be null or empty.");
        }
        if (selectedUsers == null) {
            throw new IllegalArgumentException("Selected users cannot be null.");
        }
        this.chatName = chatName;
        this.selectedUsers = List.copyOf(selectedUsers); // Ensures immutability
    }

    /**
     * Returns the name of the group chat.
     *
     * @return The chat name.
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * Returns the list of selected users.
     *
     * @return The list of users in the group chat.
     */
    public List<User> getSelectedUsers() {
        return selectedUsers;
    }
}
