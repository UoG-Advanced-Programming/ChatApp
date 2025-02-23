package models;

import java.time.LocalDateTime;

public abstract class Chat {
    protected int id;
    protected String name;
    protected ChatType type;
    protected LocalDateTime timestamp;

    // Constructor
    public Chat(int id, String name, ChatType type, LocalDateTime timestamp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ChatType getType() { return type; }
    public void setType(ChatType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public abstract void displayChatInfo();
}
