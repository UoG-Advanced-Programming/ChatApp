package com.example.client.gui;

import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;
import com.example.common.users.User;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The Model class is responsible for managing the state of the client application.
 * It handles chat management, message handling, and user management.
 */
public class Model {
    private final User currentUser; // The current user of the application
    private Chat currentChat; // The chat currently being viewed
    private String lastRetrievedSocket = null; // The last retrieved socket address
    private final Set<User> activeUsers = new HashSet<>(); // Set of active users
    private final List<Chat> chatList = new ArrayList<>(); // List of chats
    private final Map<Chat, StringBuilder> history = new HashMap<>(); // Map of chat history
    private User coordinator; // The coordinator user
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy HH:mm"); // Formatter for timestamps

    /**
     * Constructor for creating a new Model.
     *
     * @param currentUser The current user of the application
     */
    public Model(User currentUser) {
        this.currentUser = currentUser;
    }

    // Chat Management

    /**
     * Adds a chat to the chat list.
     *
     * @param chat The chat to add
     */
    public void addChat(Chat chat) {
        if (!hasChat(chat)) {
            chatList.add(chat);
            history.put(chat, new StringBuilder()); // Initialize cache for this chat
        }
    }

    /**
     * Gets the current chat being viewed.
     *
     * @return The current chat
     */
    public Chat getCurrentChat() {
        return currentChat;
    }

    /**
     * Sets the current chat being viewed.
     *
     * @param currentChat The chat to set as current
     */
    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    /**
     * Removes a chat from the chat list.
     *
     * @param chat The chat to remove
     */
    public void removeChat(Chat chat) {
        if (hasChat(chat)) {
            chatList.remove(chat);
            history.remove(chat); // Remove cached history
        }
    }

    /**
     * Gets the list of chats.
     *
     * @return The list of chats
     */
    public List<Chat> getChats() {
        return chatList;
    }

    /**
     * Checks if a chat exists in the chat list.
     *
     * @param chat The chat to check
     * @return True if the chat exists, false otherwise
     */
    public boolean hasChat(Chat chat) {
        return chatList.contains(chat);
    }

    /**
     * Checks if a private chat exists with a specific user.
     *
     * @param user The user to check
     * @return True if a private chat exists, false otherwise
     */
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

    /**
     * Adds a message to a chat and updates the cached history.
     *
     * @param message The message to add
     */
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

    /**
     * Gets the formatted chat history for a specific chat.
     *
     * @param chat The chat to get history for
     * @return The formatted chat history
     */
    public String getFormattedChatHistory(Chat chat) {
        return history.getOrDefault(chat, new StringBuilder()).toString();
    }

    // User Management

    /**
     * Adds an active user to the set of active users.
     *
     * @param user The user to add
     */
    public void addActiveUser(User user) {
        if (!hasActiveUser(user)) {
            activeUsers.add(user);
            chatList.getFirst().addParticipant(user); // Add the user to the general chat
        }
    }

    /**
     * Removes an active user from the set of active users.
     *
     * @param user The user to remove
     */
    public void removeActiveUser(User user) {
        if (hasActiveUser(user)) {
            activeUsers.remove(user);
        }
    }

    /**
     * Gets the last retrieved socket address.
     *
     * @return The last retrieved socket address
     */
    public String getLastRetrievedSocket() { return lastRetrievedSocket; }

    /**
     * Sets the last retrieved socket address.
     *
     * @param lastRetrievedIP The socket address to set
     */
    public void setLastRetrievedSocket(String lastRetrievedIP) { this.lastRetrievedSocket = lastRetrievedIP; }

    /**
     * Gets the set of active users.
     *
     * @return The set of active users
     */
    public Set<User> getActiveUsers() { return activeUsers; }

    /**
     * Checks if a user is in the set of active users.
     *
     * @param user The user to check
     * @return True if the user is active, false otherwise
     */
    public boolean hasActiveUser(User user) { return activeUsers.contains(user); }

    /**
     * Gets the current user of the application.
     *
     * @return The current user
     */
    public User getCurrentUser() { return currentUser; }

    /**
     * Gets the coordinator user.
     *
     * @return The coordinator user
     */
    public User getCoordinator() { return coordinator; }

    /**
     * Sets the coordinator user.
     *
     * @param user The user to set as coordinator
     */
    public void setCoordinator(User user) {
        user.setIsCoordinator(true);
        coordinator = user;
    }
}