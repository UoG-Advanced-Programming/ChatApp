package models;

import java.time.LocalDateTime;

public class UserUpdateMessage extends Communication {
    private User user;
    private UserStatus status;

    public UserUpdateMessage(String messageId, User user, UserStatus status, LocalDateTime timestamp) {
        super(messageId, timestamp);
        this.user = user;
        this.status = status;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    @Override
    public CommunicationType getType() {
        return CommunicationType.USER_UPDATE;
    }
}
