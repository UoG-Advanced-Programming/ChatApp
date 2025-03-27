package com.example.client.gui;

import com.example.common.users.User;
import java.util.List;

public class GroupChatDetails {
    private final String chatName;
    private final List<User> selectedUsers;

    public GroupChatDetails(String chatName, List<User> selectedUsers) {
        this.chatName = chatName;
        this.selectedUsers = selectedUsers;
    }

    public String getChatName() {
        return chatName;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }
}
