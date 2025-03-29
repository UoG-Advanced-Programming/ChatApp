package com.example.client.processing;

// All the stuff we need for testing
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
    // The main objects we're testing plus some test data
    private ClientTextMessageProcessor textMessageProcessor;
    private ClientUserUpdateMessageProcessor userUpdateProcessor;
    private TestChatController controller;
    private User testUser;
    private User testUser2;
    private GroupChat testGroupChat;
    private PrivateChat testPrivateChat;

    // Mock objects to avoid dealing with real GUI/network
    private ChatModel mockModel;
    private ChatView mockView;
    private ChatClient mockClient;

    @BeforeEach
    public void setUp() {
        // Create some dummy users for our tests
        testUser = new User("TestUser");
        testUser2 = new User("TestUser2");

        // Set up the message processors - these are what we're actually testing
        textMessageProcessor = new ClientTextMessageProcessor();
        userUpdateProcessor = new ClientUserUpdateMessageProcessor();

        // Need to create fake versions of all the dependencies
        mockModel = new TestChatModel(testUser);
        mockView = new TestChatView();  // This handles UI stuff we don't care about
        mockClient = new TestChatClient();  // Fake network client so we don't need a real server

        // Wire everything together with our test controller
        controller = new TestChatController(mockModel, mockView, mockClient);

        // Make a test group chat for our messages
        testGroupChat = new GroupChat("TestGroupChat");

        // We also need a private chat for DM tests
        testPrivateChat = new PrivateChat("TestPrivateChat");
        testPrivateChat.addParticipant(testUser);  // Add both users to the chat
        testPrivateChat.addParticipant(testUser2);  // so they can talk to each other
    }

    @Test
    public void testGroupChatTextMessageProcessing() {
        // Send a message to a group chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, world!");

        // Run the processor on it
        textMessageProcessor.processMessage(message, controller);

        // Make sure the controller did the right things
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called");
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called");
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called");
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage");
    }

    @Test
    public void testPrivateChatTextMessageProcessing() {
        // DM test - create a private message
        TextMessage message = new TextMessage(testPrivateChat, testUser, "Hello, testing private chat!");

        // Process the DM
        textMessageProcessor.processMessage(message, controller);

        // Did everything happen correctly?
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called");
        assertTrue(controller.hasChatBeenAdded(), "addChat method should be called");
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called");
        assertEquals(message, controller.getLastMessageShown(), "The message should be passed to showMessage");

        // Special check for private chats - they should show the sender's name
        assertEquals(testUser.getUsername(), testPrivateChat.getName(), "Private chat should be renamed to sender's username");
    }

    @Test
    public void testExistingChatTextMessageProcessing() {
        // For this test, pretend the chat already exists in the UI
        controller.setHasChat(true);

        // Send a message to an existing chat
        TextMessage message = new TextMessage(testGroupChat, testUser, "Hello, existing chat!");

        // Process it
        textMessageProcessor.processMessage(message, controller);

        // Should check if chat exists, NOT add it again, and show the message
        assertTrue(controller.hasChatBeenChecked(), "hasChat method should be called");
        assertFalse(controller.hasChatBeenAdded(), "addChat method should not be called for existing chat");
        assertTrue(controller.hasMessageBeenShown(), "showMessage method should be called");
    }

    @Test
    public void testUserOnlineMessageProcessing() {
        // Test what happens when someone comes online
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE);

        // Process the status update
        userUpdateProcessor.processMessage(message, controller);

        // Should add the user to the active users list
        assertTrue(controller.hasUserBeenAdded(), "addActiveUser method should be called");
        assertFalse(controller.hasUserBeenRemoved(), "removeActiveUser method should not be called");
        assertEquals(testUser, controller.getLastUserAdded(), "The user should be passed to addActiveUser");
    }

    @Test
    public void testUserOfflineMessageProcessing() {
        // Now test what happens when they go offline
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.OFFLINE);

        // Process the offline status
        userUpdateProcessor.processMessage(message, controller);

        // Should remove the user from the active list
        assertTrue(controller.hasUserBeenRemoved(), "removeActiveUser method should be called");
        assertFalse(controller.hasUserBeenAdded(), "addActiveUser method should not be called");
        assertEquals(testUser, controller.getLastUserRemoved(), "The user should be passed to removeActiveUser");
    }

    // Fake version of ChatModel - doesn't need to do much for our tests
    private static class TestChatModel extends ChatModel {
        public TestChatModel(User currentUser) {
            super(currentUser);  // Just pass the user to the real constructor
        }
        // We're not testing ChatModel so we don't need to override anything
    }

    // Fake view that doesn't try to create actual UI components
    private static class TestChatView extends ChatView {
        public TestChatView() {
            // Don't call super() because it probably creates real UI stuff
        }
        // No UI needed for these tests
    }

    // Fake client that doesn't try to connect to a real server
    private static class TestChatClient extends ChatClient {
        public TestChatClient() {
            super("localhost:7005");  // Need to give it something but we won't use it
        }

        @Override
        public void connectToServer() {
            // Do nothing - don't want to actually connect
        }
    }

    // This is our special test controller that lets us track method calls
    private static class TestChatController extends ChatController {
        // Flags to track which methods were called
        private boolean chatChecked = false;
        private boolean chatAdded = false;
        private boolean messageShown = false;
        private boolean userAdded = false;
        private boolean userRemoved = false;
        private boolean hasChat = false;  // This one controls what hasChat() returns

        // Keep the last objects passed to various methods so we can check them
        private TextMessage lastMessageShown = null;
        private User lastUserAdded = null;
        private User lastUserRemoved = null;

        public TestChatController(ChatModel model, ChatView view, ChatClient client) {
            super(model, view, client);
        }

        @Override
        public boolean hasChat(Chat chat) {
            chatChecked = true;  // Remember that this method was called
            return hasChat;      // Return whatever value we set for testing
        }

        public void setHasChat(boolean hasChat) {
            this.hasChat = hasChat;  // Let tests control what hasChat() returns
        }

        @Override
        public void addChat(Chat chat) {
            chatAdded = true;  // Remember this method was called
        }

        @Override
        public void showMessage(TextMessage message) {
            messageShown = true;      // Remember this method was called
            lastMessageShown = message;  // And save the message that was passed in
        }

        @Override
        public void addActiveUser(User user) {
            userAdded = true;     // Remember this method was called
            lastUserAdded = user;    // Save the user that was passed in
        }

        @Override
        public void removeActiveUser(User user) {
            userRemoved = true;     // Remember this method was called
            lastUserRemoved = user;    // Save the user that was passed in
        }

        // Getter methods so tests can check what happened
        public boolean hasChatBeenChecked() {
            return chatChecked;
        }

        public boolean hasChatBeenAdded() {
            return chatAdded;
        }

        public boolean hasMessageBeenShown() {
            return messageShown;
        }

        public boolean hasUserBeenAdded() {
            return userAdded;
        }

        public boolean hasUserBeenRemoved() {
            return userRemoved;
        }

        public TextMessage getLastMessageShown() {
            return lastMessageShown;
        }

        public User getLastUserAdded() {
            return lastUserAdded;
        }

        public User getLastUserRemoved() {
            return lastUserRemoved;
        }
    }
}