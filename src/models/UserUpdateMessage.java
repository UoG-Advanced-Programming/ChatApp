package models;

import java.time.LocalDateTime;

public class UserUpdateMessage extends Communication {
    private User user;
    private String status; // e.g., "online", "offline", "profile_updated"

    public UserUpdateMessage(String messageId, User user, String status, LocalDateTime timestamp) {
        super(messageId, timestamp);
        this.user = user;
        this.status = status;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public CommunicationType getType() {
        return CommunicationType.USER_UPDATE;
    }
}
