package com.example.common.chats;

import com.example.common.users.User;

public class PrivateChat extends Chat {
    private boolean active = true;

    public PrivateChat(String chatName) {
        super(chatName);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void displayChatInfo() {
        System.out.print("Private Chat: " + getName() + " between ");
        for (User participant : getParticipants()) {
            System.out.print(participant.getUsername() + " ");
        }
        System.out.println();
    }

    @Override
    public ChatType getType() {
        return ChatType.PRIVATE;
    }
}
