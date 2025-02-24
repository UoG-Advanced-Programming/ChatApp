package models;

import java.time.LocalDateTime;

public class PrivateChat extends Chat {
    private User user1Id;
    private User user2Id;

    // Constructor
    public PrivateChat(String chatId, String chatName, User user1Id, User user2Id) {
        super(chatId, chatName, LocalDateTime.now());
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    // Getters and Setters
    public User getUser1Id() { return user1Id; }
    public void setUser1Id(User user1Id) { this.user1Id = user1Id; }

    public User getUser2Id() { return user2Id; }
    public void setUser2Id(User user2Id) { this.user2Id = user2Id; }

    @Override
    public void displayChatInfo() {
        System.out.println("Private Chat: " + getName() + " between User " + user1Id + " and User " + user2Id);
    }

    @Override
    public ChatType getType() {
        return ChatType.PRIVATE;
    }
}
