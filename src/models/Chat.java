package models;

import java.time.LocalDateTime;

public abstract class Chat {
    protected String id;
    protected String name;
    public ChatType type;
    protected LocalDateTime timestamp;

    // Constructor
    public Chat(String id, String name, ChatType type, LocalDateTime timestamp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public abstract void displayChatInfo();
    public abstract ChatType getType();
}
