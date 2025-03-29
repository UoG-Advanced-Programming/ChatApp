package com.example.client.processing;

// Import statements for various classes used in the tests
import com.example.client.gui.ChatController;
import com.example.client.gui.ChatModel;
import com.example.client.gui.ChatView;
import com.example.client.network.ChatClient;
import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.TextMessage;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageProcessorTest {
    // Declare variables for message processors, controller, test users, and chats
    private ClientTextMessageProcessor textMessageProcessor;
    private ClientUserUpdateMessageProcessor userUpdateProcessor;
    private TestChatController controller;
    private User testUser;
    private User testUser2;
    private GroupChat testGroupChat;
    private PrivateChat testPrivateChat;

    // Test implementations of dependencies
    private ChatModel mockModel;
    private ChatView mockView;
    private ChatClient mockClient;

    @BeforeEach
    public void setUp() {
        // Create test users before each test
        testUser = new User("TestUser"); // Create a new user named "TestUser"
        testUser2 = new User("TestUser2"); // Create a new user named "TestUser2"

        // Initialize message processors for text messages and user updates
        textMessageProcessor = new ClientTextMessageProcessor(); // Initialize text message processor
        userUpdateProcessor = new ClientUserUpdateMessageProcessor(); // Initialize user update message processor

        // Create mock dependencies for ChatController
        mockModel = new TestChatModel(testUser); // Create a mock ChatModel with testUser
        mockView = new TestChatView(); // Create a mock ChatView
        mockClient = new TestChatClient(); // Create a mock ChatClient

        // Initialize the ChatController with mock dependencies
        controller = new TestChatController(mockModel, mockView, mockClient); // Initialize the ChatController with mocks

        // Create a test group chat
        testGroupChat = new GroupChat("TestGroupChat"); // Create a group chat named "TestGroupChat"

        // Create a private chat and add participants
        testPrivateChat = new PrivateChat("TestPrivateChat"); // Create a private chat named "TestPrivateChat"
        testPrivateChat.addParticipant(testUser); // Add testUser to the private chat
        testPrivateChat.addParticipant(testUser2); // Add testUser2 to the private chat
    }

    @Test
    public void testGroupChatTextMessageProcessing() {
        // Create a test message for a group chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, world!"); // Create a new text message

        // Process the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verify controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Verify if hasChat method was called
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called"); // Verify if addChat method was called
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Verify if showMessage method was called
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage"); // Verify if the message was passed to showMessage
    }

    @Test
    public void testPrivateChatTextMessageProcessing() {
        // Create a test message for a private chat
        TextMessage message = new TextMessage(testPrivateChat, testUser, "Hello, testing private chat!"); // Create a new text message for private chat

        // Process the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verify controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Verify if hasChat method was called
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called"); // Verify if addChat method was called
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Verify if showMessage method was called
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage"); // Verify if the message was passed to showMessage

        // Verify if the private chat was renamed to the sender's username
        assertEquals(testUser.getUsername(), testPrivateChat.getName(), "Private chat should be renamed to sender's username"); // Verify if the private chat was renamed
    }

    @Test
    public void testExistingChatTextMessageProcessing() {
        // Set up the controller to indicate the chat already exists
        controller.setHasChat(true); // Set the controller to indicate the chat already exists

        // Create a test message for an existing chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, existing chat!"); // Create a new text message for an existing chat

        // Process the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verify controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Verify if hasChat method was called
        assertFalse(controller.hasChatBeenAdded(), "addChat method should not be called for existing chat"); // Verify if addChat method was not called for existing chat
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Verify if showMessage method was called
    }

    @Test
    public void testUserOnlineMessageProcessing() {
        // Create a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE); // Create a new user status update message

        // Process the message
        userUpdateProcessor.processMessage(message, controller); // Process the created status update message

        // Verify controller interactions
        assertTrue(controller.hasUserBeenAdded(), "addActiveUser method should be called"); // Verify if addActiveUser method was called
        assertFalse(controller.hasUserBeenRemoved(), "removeActiveUser method should not be called"); // Verify if removeActiveUser method was not called
        assertEquals(testUser, controller.getLastUserAdded(), "The user should be passed to addActiveUser"); // Verify if the user was passed to addActiveUser
    }

    @Test
    public void testUserOfflineMessageProcessing() {
        // Create a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.OFFLINE); // Create a new user status update message

        // Process the message
        userUpdateProcessor.processMessage(message, controller); // Process the created status update message

        // Verify controller interactions
        assertTrue(controller.hasUserBeenRemoved(), "removeActiveUser method should be called"); // Verify if removeActiveUser method was called
        assertFalse(controller.hasUserBeenAdded(), "addActiveUser method should not be called"); // Verify if addActiveUser method was not called
        assertEquals(testUser, controller.getLastUserRemoved(), "The user should be passed to removeActiveUser"); // Verify if the user was passed to removeActiveUser
    }

    // Test implementation of ChatModel
    private static class TestChatModel extends ChatModel {
        public TestChatModel(User currentUser) {
            super(currentUser); // Call the parent constructor with currentUser
        }

        // Override any methods that might be called during tests
    }

    // Test implementation of ChatView
    private static class TestChatView extends ChatView {
        // Override constructor if needed
        public TestChatView() {
            // No call to super if it requires parameters
        }

        // Implement any necessary methods from ChatView
    }

    // Test implementation of ChatClient
    private static class TestChatClient extends ChatClient {
        public TestChatClient() {
            // Fix: ChatClient appears to only take a hostname string, not hostname and port
            super("localhost:7005"); // Using the port you provided (7005)
        }

        // Override methods that might be called during tests
        @Override
        public void connectToServer() {
            // Do nothing in the test environment
        }
    }

    // Custom test implementation of ChatController
    private static class TestChatController extends ChatController {
        private boolean chatChecked = false;
        private boolean chatAdded = false;
        private boolean messageShown = false;
        private boolean userAdded = false;
        private boolean userRemoved = false;
        private boolean hasChat = false;

        private TextMessage lastMessageShown = null;
        private User lastUserAdded = null;
        private User lastUserRemoved = null;

        public TestChatController(ChatModel model, ChatView view, ChatClient client) {
            super(model, view, client); // Call the parent constructor with model, view, and client
        }

        @Override
        public boolean hasChat(Chat chat) {
            chatChecked = true; // Mark chat as checked when this method is called
            return hasChat; // Return the hasChat value
        }

        public void setHasChat(boolean hasChat) {
            this.hasChat = hasChat; // Set the hasChat value
        }

        @Override
        public void addChat(Chat chat) {
            chatAdded = true; // Mark chat as added when this method is called
        }

        @Override
        public void showMessage(TextMessage message) {
            messageShown = true; // Mark message as shown when this method is called
            lastMessageShown = message; // Store the last message shown
        }

        @Override
        public void addActiveUser(User user) {
            userAdded = true; // Mark user as added when this method is called
            lastUserAdded = user; // Store the last user added
        }

        @Override
        public void removeActiveUser(User user) {
            userRemoved = true; // Mark user as removed when this method is called
            lastUserRemoved = user; // Store the last user removed
        }

        public boolean hasChatBeenChecked() {
            return chatChecked; // Return the chatChecked value
        }

        public boolean hasChatBeenAdded() {
            return chatAdded; // Return the chatAdded value
        }

        public boolean hasMessageBeenShown() {
            return messageShown; // Return the messageShown value
        }

        public boolean hasUserBeenAdded() {
            return userAdded; // Return the userAdded value
        }

        public boolean hasUserBeenRemoved() {
            return userRemoved; // Return the userRemoved value
        }

        public TextMessage getLastMessageShown() {
            return lastMessageShown; // Return the last message shown
        }

        public User getLastUserAdded() {
            return lastUserAdded; // Return the last user added
        }

        public User getLastUserRemoved() {
            return lastUserRemoved; // Return the last user removed
        }
    }
}