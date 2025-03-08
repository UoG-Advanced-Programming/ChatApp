package com.example.common.users;

import com.example.common.utils.IDGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}