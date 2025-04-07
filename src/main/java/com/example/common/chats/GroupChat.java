package com.example.common.chats;

import com.example.common.users.User;

/**
 * The GroupChat class extends the Chat class and represents a group chat
 * with multiple participants.
 */
public class GroupChat extends Chat {
    protected String name; // Name of the chat

    /**
     * Constructor for creating a new GroupChat.
     *
     * @param name The name of the chat
     */
    public GroupChat(String name) {
        this.name = name; // Call the parent constructor with the chat name
    }

    /**
     * Returns the display name of the group chat, which is the name provided during creation.
     *
     * @param currentUser The user requesting the display name (not used here).
     * @return The name of the group chat.
     */
    @Override
    public String getDisplayName(User currentUser) {
        return name;
    }

    /**
     * Gets the type of the chat.
     *
     * @return The type of the chat, which is GROUP
     */
    @Override
    public ChatType getType() { return ChatType.GROUP; }
}