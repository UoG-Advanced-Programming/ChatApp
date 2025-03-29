package com.example.common.messages;

import com.example.common.chats.Chat;
import com.example.common.users.User;

/**
 * The TextMessage class extends the Communication class and represents a text message
 * sent in a chat by a user.
 */
public class TextMessage extends Communication {
    private Chat chat; // The chat where the message is sent
    private User sender; // The user who sent the message
    private String content; // The content of the message

    /**
     * Constructor for creating a new TextMessage.
     * Initializes the message with the given chat, sender, and content.
     *
     * @param chat    The chat where the message is sent
     * @param sender  The user who sent the message
     * @param content The content of the message
     */
    public TextMessage(Chat chat, User sender, String content) {
        super(CommunicationType.TEXT); // Call the parent constructor with the communication type TEXT
        this.chat = chat; // Set the chat
        this.sender = sender; // Set the sender
        this.content = content; // Set the content
    }

    /**
     * Gets the chat where the message is sent.
     *
     * @return The chat where the message is sent
     */
    public Chat getChat() { return chat; }

    /**
     * Sets the chat where the message is sent.
     *
     * @param chat The chat where the message is sent
     */
    public void setChat(Chat chat) { this.chat = chat; }

    /**
     * Gets the user who sent the message.
     *
     * @return The user who sent the message
     */
    public User getSender() { return sender; }

    /**
     * Sets the user who sent the message.
     *
     * @param sender The user who sent the message
     */
    public void setSender(User sender) { this.sender = sender; }

    /**
     * Gets the content of the message.
     *
     * @return The content of the message
     */
    public String getContent() { return content; }

    /**
     * Sets the content of the message.
     *
     * @param content The content of the message
     */
    public void setContent(String content) { this.content = content; }
}