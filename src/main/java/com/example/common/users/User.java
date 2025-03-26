package com.example.common.users;

import com.example.common.utils.IDGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private String id;
    private String username;
    private LocalDateTime createdAt;
    private boolean isCoordinator;

    // Constructor
    public User(String username) {
        this.id = IDGenerator.generateUUID();
        this.username = username;
        this.createdAt = LocalDateTime.now();
        this.isCoordinator = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean getIsCoordinator() { return isCoordinator; }
    public void setIsCoordinator(boolean isCoordinator) { this.isCoordinator = isCoordinator; }

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