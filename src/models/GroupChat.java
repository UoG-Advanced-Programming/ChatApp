package models;

import java.util.ArrayList;
import java.util.List;

public class GroupChat extends Chat {
    private final List<User> users;

    // Constructor
    public GroupChat(String chatId, String chatName) {
        super(chatId, chatName, ChatType.GROUP, java.time.LocalDateTime.now());
        this.users = new ArrayList<>();
    }

    // Getter
    public List<User> getUsers() { return users; }

    // Method to add a user to the group chat
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            System.out.println("User " + user.getUsername() + " is added to the group chat: " + getName());
        } else {
            System.out.println("User " + user.getUsername() + " is already in the group chat: " + getName());
        }
    }

    // Method to remove a user from the group chat
    public void removeUser(User user) {
        if (users.remove(user)) {
            System.out.println("User " + user.getUsername() + " is removed from the group chat: " + getName());
        } else {
            System.out.println("User " + user.getUsername() + " is not in the group chat: " + getName());
        }
    }

    @Override
    public void displayChatInfo() {
        System.out.print("Group Chat: " + getName() + " with members: ");
        for (User user : users) {
            System.out.print(user.getUsername() + " ");
        }
        System.out.println();
    }
}
