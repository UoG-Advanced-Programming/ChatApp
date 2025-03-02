package models;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

    // Constructor
    public User(String username, String password) {
        this.id = IDGenerator.generateUUID();
        this.username = username;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
