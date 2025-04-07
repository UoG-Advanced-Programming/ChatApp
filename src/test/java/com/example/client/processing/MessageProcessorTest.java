package com.example.client.processing;

// Import statements for various classes used in the tests
import com.example.client.gui.Controller;
import com.example.client.gui.Model;
import com.example.client.gui.View;
import com.example.client.network.Client;
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
    // Declaring variables for message processors, controller, test users, and chats
    private ClientTextMessageProcessor textMessageProcessor;
    private ClientUserUpdateMessageProcessor userUpdateProcessor;
    private TestController controller;
    private User testUser;
    private User testUser2;
    private GroupChat testGroupChat;
    private PrivateChat testPrivateChat;

    // Test implementations of dependencies
    private Model mockModel;
    private View mockView;
    private Client mockClient;

    @BeforeEach
    public void setUp() {
        // Creating test users before each test since they're needed as dependencies
        testUser = new User("TestUser"); // Create a new user named "TestUser"
        testUser2 = new User("TestUser2"); // Create a new user named "TestUser2"

        // Initializing message processors for text messages and user updates
        textMessageProcessor = new ClientTextMessageProcessor(); // Initialize text message processor
        userUpdateProcessor = new ClientUserUpdateMessageProcessor(); // Initialize user update message processor

        // Creating mock dependencies for Controller with properly initialized values
        mockModel = new TestModel(testUser); // Create a mock Model with testUser
        mockView = new TestView(); // Create a mock View
        mockClient = new TestClient(); // Create a mock Client

        // Creating the controller with mock dependencies
        controller = new TestController(mockModel, mockView, mockClient); // Initialize the Controller with mocks

        // Creating a test group chat
        testGroupChat = new GroupChat("TestGroupChat"); // Create a group chat named "TestGroupChat"

        // Creating a private chat and adding participants explicitly
        testPrivateChat = new PrivateChat(); // Create a private chat
        testPrivateChat.addParticipant(testUser); // Add testUser to the private chat
        testPrivateChat.addParticipant(testUser2); // Add testUser2 to the private chat
    }

    @Test
    public void testGroupChatTextMessageProcessing() {
        // Creating a test message for a group chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, world!"); // Create a new text message

        // Processing the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verifying controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Check if hasChat method was called
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called"); // Check if addChat method was called
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Check if showMessage method was called
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage"); // Verify the message was passed to showMessage
    }

    @Test
    public void testPrivateChatTextMessageProcessing() {
        // Creating a test message for a private chat
        TextMessage message = new TextMessage(testPrivateChat, testUser, "Hello, testing private chat!"); // Create a new text message for private chat

        // Processing the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verifying controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Check if hasChat method was called
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called"); // Check if addChat method was called
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Check if showMessage method was called
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage"); // Verify the message was passed to showMessage

        // Verifying the private chat was renamed to the sender's username
        assertEquals(testUser.getUsername(), testPrivateChat.getDisplayName(testUser2), "Private chat should be renamed to sender's username"); // Check if the private chat was renamed
    }

    @Test
    public void testExistingChatTextMessageProcessing() {
        // Setting up the controller to indicate the chat already exists
        controller.setHasChat(true); // Set the controller to indicate the chat already exists

        // Creating a test message for an existing chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, existing chat!"); // Create a new text message for an existing chat

        // Processing the message
        textMessageProcessor.processMessage(message, controller); // Process the created message

        // Verifying controller interactions
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called"); // Check if hasChat method was called
        assertFalse(controller.hasChatBeenAdded(), "addChat method should not be called for existing chat"); // Check if addChat method was not called for existing chat
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called"); // Check if showMessage method was called
    }

    @Test
    public void testUserOnlineMessageProcessing() {
        // Creating a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE); // Create a new user status update message

        // Processing the message
        userUpdateProcessor.processMessage(message, controller); // Process the created status update message

        // Verifying controller interactions
        assertTrue(controller.hasUserBeenAdded(), "addActiveUser method should be called"); // Check if addActiveUser method was called
        assertFalse(controller.hasUserBeenRemoved(), "removeActiveUser method should not be called"); // Check if removeActiveUser method was not called
        assertEquals(testUser, controller.getLastUserAdded(), "The user should be passed to addActiveUser"); // Verify the user was passed to addActiveUser
    }

    @Test
    public void testUserOfflineMessageProcessing() {
        // Creating a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.OFFLINE); // Create a new user status update message

        // Processing the message
        userUpdateProcessor.processMessage(message, controller); // Process the created status update message

        // Verifying controller interactions
        assertTrue(controller.hasUserBeenRemoved(), "removeActiveUser method should be called"); // Check if removeActiveUser method was called
        assertFalse(controller.hasUserBeenAdded(), "addActiveUser method should not be called"); // Check if addActiveUser method was not called
        assertEquals(testUser, controller.getLastUserRemoved(), "The user should be passed to removeActiveUser"); // Verify the user was passed to removeActiveUser
    }

    // Test implementation of Model
    private static class TestModel extends Model {
        public TestModel(User currentUser) {
            super(currentUser); // Call the parent constructor with currentUser
        }

        // Override any methods that might be called during tests
    }

    // Test implementation of View
    private static class TestView extends View {
        // Override constructor if needed
        public TestView() {
            // No call to super if it requires parameters
        }

    }

    // Test implementation of Client
    private static class TestClient extends Client {
        public TestClient() {
            // Fix: Client appears to only take a hostname string, not hostname and port
            super("localhost:7005"); // Using the port 7005
        }

        // Override methods that might be called during tests
        @Override
        public void connectToServer() {
            // Do nothing in test
        }
    }

    // Custom test implementation of Controller
    private static class TestController extends Controller {
        private boolean chatChecked = false;
        private boolean chatAdded = false;
        private boolean messageShown = false;
        private boolean userAdded = false;
        private boolean userRemoved = false;
        private boolean hasChat = false;

        private TextMessage lastMessageShown = null;
        private User lastUserAdded = null;
        private User lastUserRemoved = null;

        public TestController(Model model, View view, Client client) {
            super(model, view, client); // Call the parent constructor with model, view, and client
        }

        @Override
        public boolean hasChat(Chat chat) {
            chatChecked = true; // Set chatChecked to true when this method is called
            return hasChat; // Return the hasChat value
        }

        public void setHasChat(boolean hasChat) {
            this.hasChat = hasChat; // Set the hasChat value
        }

        @Override
        public void addChat(Chat chat) {
            chatAdded = true; // Set chatAdded to true when this method is called
        }

        @Override
        public void showMessage(TextMessage message) {
            messageShown = true; // Set messageShown to true when this method is called
            lastMessageShown = message; // Store the last message shown
        }

        @Override
        public void addActiveUser(User user) {
            userAdded = true; // Set userAdded to true when this method is called
            lastUserAdded = user; // Store the last user added
        }

        @Override
        public void removeActiveUser(User user) {
            userRemoved = true; // Set userRemoved to true when this method is called
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
