package models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public abstract class Chat {
    protected String id;
    protected String name;
    protected LocalDateTime timestamp;
    protected final Set<User> participants;

    // Constructor
    public Chat(String name) {
        this.id = IDGenerator.generateUUID();
        this.name = name;
        this.timestamp = LocalDateTime.now();
        this.participants = new HashSet<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Methods for managing participants
    public void addParticipant(User participant) {
        if (participants.add(participant)) {
            System.out.println("Participant " + participant.getUsername() + " added to chat: " + getName());
        } else {
            System.out.println("Participant " + participant.getUsername() + " is already in the chat: " + getName());
        }
    }

    public void removeParticipant(User participant) {
        if (participants.remove(participant)) {
            System.out.println("Participant " + participant.getUsername() + " removed from chat: " + getName());
        } else {
            System.out.println("Participant " + participant.getUsername() + " is not in the chat: " + getName());
        }
    }

    public Set<User> getParticipants() {
        return participants;
    }

    // Abstract methods
    public abstract void displayChatInfo();
    public abstract ChatType getType();
}