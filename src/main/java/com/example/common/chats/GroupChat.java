package com.example.common.chats;

import com.example.common.users.User;

/**
 * The GroupChat class extends the Chat class and represents a group chat
 * with multiple participants.
 */
public class GroupChat extends Chat {

    /**
     * Constructor for creating a new GroupChat.
     *
     * @param chatName The name of the chat
     */
    public GroupChat(String chatName) {
        super(chatName); // Call the parent constructor with the chat name
    }

    /**
     * Displays information about the group chat.
     * Overrides the displayChatInfo method in the Chat class.
     */
    @Override
    public void displayChatInfo() {
        System.out.print("Group Chat: " + getName() + " with participants: "); // Print the chat name
        for (User participant : getParticipants()) { // Iterate through the participants
            System.out.print(participant.getUsername() + " "); // Print each participant's username
        }
        System.out.println(); // Print a new line at the end
    }

    /**
     * Gets the type of the chat.
     *
     * @return The type of the chat, which is GROUP
     */
    @Override
    public ChatType getType() {
        return ChatType.GROUP; // Return the chat type as GROUP
    }
}