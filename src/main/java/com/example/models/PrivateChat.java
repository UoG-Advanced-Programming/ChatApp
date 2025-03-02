package com.example.models;

public class PrivateChat extends Chat {
    public PrivateChat(String chatName) {
        super(chatName);
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
