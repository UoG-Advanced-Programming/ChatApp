package models;

import java.time.LocalDateTime;

public class Message {
    private String messageId;
    private Chat chat;
    private User sender;
    private String content;
    private LocalDateTime timestamp;

    // Constructor
    public Message(String messageId, Chat chat, User sender, String content, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
