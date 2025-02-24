package models;

import java.time.LocalDateTime;

public class TextMessage extends Communication {
    private Chat chat;
    private User sender;
    private String content;

    public TextMessage(String messageId, Chat chat, User sender, String content, LocalDateTime timestamp) {
        super(messageId, timestamp);
        this.chat = chat;
        this.sender = sender;
        this.content = content;
    }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public CommunicationType getType() {
        return CommunicationType.TEXT;
    }
}
