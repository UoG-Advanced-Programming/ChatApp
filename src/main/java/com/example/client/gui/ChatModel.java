package com.example.client.gui;

import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;
import com.example.common.users.User;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatModel {
    private final User currentUser;
    private Chat currentChat;
    private String lastRetrievedSocket = null;
    private final Set<User> activeUsers = new HashSet<>();
    private final List<Chat> chatList = new ArrayList<>();
    private final Map<Chat, StringBuilder> history = new HashMap<>();
    private User coordinator;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy HH:mm");

    public ChatModel(User currentUser) {
        this.currentUser = currentUser;
    }

    // Chat Management
    public void addChat(Chat chat) {
        if (!hasChat(chat)) {
            chatList.add(chat);
            history.put(chat, new StringBuilder()); // Initialize cache for this chat
        }
    }

    public Chat getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    public void removeChat(Chat chat) {
        if (hasChat(chat)) {
            chatList.remove(chat);
            history.remove(chat); // Remove cached history
        }
    }

    public List<Chat> getChats() {
        return chatList;
    }

    public boolean hasChat(Chat chat) {
        return chatList.contains(chat);
    }

    public boolean hasPrivateChatWith(User user) {
        for (Chat chat : chatList) {
            if (chat instanceof PrivateChat privateChat) {
                if (privateChat.getParticipants().contains(user)) {
                    return true; // A private chat already exists between these users
                }
            }
        }
        return false; // No private chat exists between these users
    }

    // Message Handling
    public void addMessageToChat(TextMessage message) {
        Chat chat = message.getChat();

        // Update the cached history
        StringBuilder cache = history.computeIfAbsent(chat, k -> new StringBuilder());
        cache.append("[")
                .append(TIMESTAMP_FORMATTER.format(message.getTimestamp()))
                .append("] ")
                .append(message.getSender().getUsername())
                .append(": ")
                .append(message.getContent())
                .append("\n");
    }

    public String getFormattedChatHistory(Chat chat) {
        return history.getOrDefault(chat, new StringBuilder()).toString();
    }

    // User Management
    public void addActiveUser(User user) {
        if (!hasActiveUser(user)) {
            activeUsers.add(user);
            chatList.getFirst().addParticipant(user); // Add the user to the general chat
        }
    }

    public void removeActiveUser(User user) {
        if (hasActiveUser(user)) {
            activeUsers.remove(user);
        }
    }

    public String getLastRetrievedSocket() { return lastRetrievedSocket; }

    public void setLastRetrievedSocket(String lastRetrievedIP) { this.lastRetrievedSocket = lastRetrievedIP; }

    public Set<User> getActiveUsers() { return activeUsers; }

    public boolean hasActiveUser(User user) { return activeUsers.contains(user); }

    public User getCurrentUser() { return currentUser; }

    public User getCoordinator() { return coordinator; }

    public void setCoordinator(User user) {
        user.setIsCoordinator(true);
        coordinator = user;
    }
}
