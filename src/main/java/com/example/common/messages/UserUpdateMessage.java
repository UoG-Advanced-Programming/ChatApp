package com.example.common.messages;

import com.example.common.users.User;

public class UserUpdateMessage extends Communication {
    private User user;
    private UserStatus status;

    public UserUpdateMessage(User user, UserStatus status) {
        super(CommunicationType.USER_UPDATE);
        this.user = user;
        this.status = status;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
