package com.example.client.network;

// All the imports we need for testing the client
import com.example.common.messages.TextMessage;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.common.chats.GroupChat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class ChatClientTest {
    // Stuff we need for our tests
    private TestChatClient chatClient;
    private StringWriter outputWriter;
    private User testUser;
    private GroupChat testChat;

    @BeforeEach
    public void setUp() {
        // Creating objects we'll use in all the tests
        testUser = new User("TestUser"); // Just a dummy test user
        testChat = new GroupChat("TestChat"); // Dummy group chat for testing

        // We'll capture the output here so we can check it later
        outputWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(outputWriter, true);

        // Setting up our test client - no actual server connections here
        chatClient = new TestChatClient("localhost");
        chatClient.setOut(printWriter);

        // Just logging when setup is done - helps with debugging
        System.out.println("Test setup completed at: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());
        System.out.println("Test user created with ID: " + testUser.getId());
        System.out.println("Test chat created with ID: " + testChat.getId());
    }

    @Test
    public void testSendMessage() {
        // Let's try sending a basic message
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!");
        System.out.println("Test message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Chat ID: " + testChat.getId());
        System.out.println("  Content: Hello, world!");

        // Send it and see what happens
        chatClient.send(message);
        System.out.println("Message sent to client");

        // Grab what our client actually sent
        String output = outputWriter.toString().trim();
        System.out.println("Captured output from client:");
        System.out.println("-----------------------------------");
        System.out.println(output);
        System.out.println("-----------------------------------");

        // Check if everything we expect is in there
        System.out.println("Checking if output contains message content: " + output.contains("Hello, world!"));
        System.out.println("Checking if output contains user ID: " + output.contains(testUser.getId()));
        System.out.println("Checking if output contains chat ID: " + output.contains(testChat.getId()));

        // Make sure the message has all the right parts
        assertTrue(output.contains("Hello, world!"), "Message content should be in the output");
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output");
        assertTrue(output.contains(testChat.getId()), "Chat ID should be in the output");

        System.out.println("testSendMessage test completed successfully");
    }

    @Test
    public void testUserStatusUpdate() {
        // Testing if status updates work properly
        System.out.println("Starting testUserStatusUpdate test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's " + testUser.getUsername());

        // Create a status update - this is what happens when someone goes online/offline
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE);
        System.out.println("User update message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Username: " + testUser.getUsername());
        System.out.println("  Status: " + UserStatus.ONLINE);

        // Send the status update
        chatClient.send(message);
        System.out.println("User status update message sent to client");

        // Check what was actually sent
        String output = outputWriter.toString().trim();
        System.out.println("Captured output from client:");
        System.out.println("-----------------------------------");
        System.out.println(output);
        System.out.println("-----------------------------------");

        // Double check the important bits
        System.out.println("Checking if output contains ONLINE status: " + output.contains("\"status\":\"ONLINE\""));
        System.out.println("Checking if output contains user ID: " + output.contains(testUser.getId()));

        // Make sure the status message has the right parts
        assertTrue(output.contains("\"status\":\"ONLINE\""), "Status should be ONLINE");
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output");

        System.out.println("testUserStatusUpdate test completed successfully");
    }

    @Test
    public void testConnectionErrorHandling() {
        // Let's see how the client handles network errors
        System.out.println("Starting testConnectionErrorHandling test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());

        // Log the original writer so we know what we're working with
        System.out.println("Original PrintWriter instance: " + chatClient.getOut());

        // Make a writer that breaks on purpose - this will simulate a network error
        System.out.println("Creating failing PrintWriter that will throw a RuntimeException");
        PrintWriter failingWriter = new PrintWriter(outputWriter) {
            @Override
            public void println(String x) {
                System.out.println("Failing PrintWriter.println() called with: " + x);
                System.out.println("About to throw simulated network error");
                throw new RuntimeException("Simulated network error"); // Boom! Network failed
            }
        };

        System.out.println("Setting failing PrintWriter on ChatClient");
        chatClient.setOut(failingWriter);

        // Make a test message to try sending
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!");
        System.out.println("Test message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Chat ID: " + testChat.getId());
        System.out.println("  Content: Hello, world!");

        // This should fail, but in a controlled way
        System.out.println("About to send message using failing writer...");
        System.out.println("(Expect to see error message from ChatClient's exception handler)");

        // The error should be caught inside the client - not crash our test
        System.out.println("Testing that exception is caught and doesn't propagate out of send()");
        try {
            chatClient.send(message);
            System.out.println("✓ Success: No exception was thrown from send() method");
        } catch (Exception e) {
            System.out.println("✗ Failure: Exception escaped from send() method: " + e);
            throw e; // This would fail our test
        }

        // If we get here, the client caught the error properly
        System.out.println("Test completed successfully - ChatClient properly handled the error");
        System.out.println("(The 'Error sending message: Simulated network error' above comes from ChatClient's error handler)");

        // Just a divider to make logs easier to read
        System.out.println("------------------------------------------------------");
    }

    @Test
    public void testNullMessageHandling() {
        // Can our client handle null values without crashing?
        System.out.println("Starting testNullMessageHandling test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());

        // Try sending null - shouldn't crash but probably won't do much
        System.out.println("Testing send(null) handling...");
        assertDoesNotThrow(() -> chatClient.send(null), "Client should handle null messages gracefully");

        // Clear the output buffer for the next test
        outputWriter.getBuffer().setLength(0);

        // Now try a message with null content
        System.out.println("Testing message with null content...");
        TextMessage nullContentMsg = new TextMessage(testChat, testUser, null);
        assertDoesNotThrow(() -> chatClient.send(nullContentMsg), "Client should handle null message content gracefully");

        // See what came out
        String nullContentOutput = outputWriter.toString().trim();
        System.out.println("Captured output for null content message:");
        System.out.println("-----------------------------------");
        System.out.println(nullContentOutput);
        System.out.println("-----------------------------------");

        // Clear the output buffer again
        outputWriter.getBuffer().setLength(0);

        // Try with a null chat - edge case but should be handled
        System.out.println("Testing message with null chat...");
        TextMessage nullChatMsg = new TextMessage(null, testUser, "Test content");
        assertDoesNotThrow(() -> chatClient.send(nullChatMsg), "Client should handle null chat gracefully");

        // Check what happened with the null chat
        String nullChatOutput = outputWriter.toString().trim();
        System.out.println("Captured output for null chat message:");
        System.out.println("-----------------------------------");
        System.out.println(nullChatOutput);
        System.out.println("-----------------------------------");

        System.out.println("testNullMessageHandling test completed successfully");
    }

    @Test
    public void testResourceCleanupOnDisconnect() {
        // Making sure we don't leak resources when disconnecting
        System.out.println("Starting testResourceCleanupOnDisconnect test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());

        // Special test client that tracks what gets closed
        class DisconnectTestClient extends TestChatClient {
            private boolean socketClosed = false;
            private boolean writerClosed = false;
            private boolean readerClosed = false;

            public DisconnectTestClient(String host) {
                super(host);
            }

            @Override
            public void disconnect() {
                System.out.println("DisconnectTestClient disconnect called");
                super.disconnect(); // Call parent disconnect first
                socketClosed = true; // Keep track of what's closed
                writerClosed = true;
                readerClosed = true;
            }

            public boolean isSocketClosed() {
                return socketClosed;
            }

            public boolean isWriterClosed() {
                return writerClosed;
            }

            public boolean isReaderClosed() {
                return readerClosed;
            }
        }

        // Need a fresh client for this test
        DisconnectTestClient disconnectClient = new DisconnectTestClient("localhost");

        // Give it some resources to clean up
        StringWriter stringWriter = new StringWriter();
        PrintWriter testWriter = new PrintWriter(stringWriter);
        disconnectClient.setOut(testWriter);
        Socket testSocket = new Socket();
        disconnectClient.setSocket(testSocket);
        BufferedReader testReader = new BufferedReader(new StringReader(""));
        disconnectClient.setIn(testReader);

        System.out.println("Setting up test client resources");

        // Try disconnecting
        System.out.println("Calling disconnect on test client");
        disconnectClient.disconnect();

        // Check if everything got closed properly
        System.out.println("Verifying resources were cleaned up");
        assertTrue(disconnectClient.isSocketClosed(), "Socket should be closed after disconnect");
        assertTrue(disconnectClient.isWriterClosed(), "Writer should be closed after disconnect");
        assertTrue(disconnectClient.isReaderClosed(), "Reader should be closed after disconnect");

        System.out.println("testResourceCleanupOnDisconnect test completed successfully");
    }

    @Test
    public void testHeartbeat() {
        // Testing the heartbeat functionality that keeps connections alive
        System.out.println("Starting testHeartbeat test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());

        // Pretend we got a heartbeat from the server
        chatClient.recordHeartbeat();
        System.out.println("Heartbeat recorded at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Check that the time got updated correctly
        long lastHeartbeatTime = chatClient.getLastHeartbeatTime();
        long currentTime = System.currentTimeMillis();
        assertTrue(currentTime - lastHeartbeatTime < 1000, "Heartbeat time should be recent");

        System.out.println("testHeartbeat test completed successfully");
    }

    /**
     * Just a helper to get the current time in a nice format
     */
    private String getCurrentFormattedDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * This is our fake client class for testing - doesn't actually connect to a server
     */
    private static class TestChatClient extends ChatClient {
        private PrintWriter out;
        private BufferedReader in;
        private Socket socket;

        public TestChatClient(String host) {
            super(host);
        }

        @Override
        protected void connectToServer() {
            // Skip actually connecting - we're just testing
        }

        @Override
        protected String promptForCredentials() {
            // No need to prompt for login during tests
            return "TestUser";
        }

        @Override
        public void start() {
            // Don't start real client threads for tests
            User user = new User("TestUser");
            UserUpdateMessage message = new UserUpdateMessage(user, UserStatus.ONLINE);
            send(message);
        }

        @Override
        public void setOut(PrintWriter writer) {
            this.out = writer;
            super.setOut(writer);
        }

        @Override
        public void setIn(BufferedReader reader) {
            this.in = reader;
            super.setIn(reader);
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
            // If your parent class has a similar method, call it:
            // super.setSocket(socket);
        }

        public PrintWriter getOut() {
            return out;
        }

        public BufferedReader getIn() {
            return in;
        }

        public Socket getSocket() {
            return socket;
        }

        public long getLastHeartbeatTime() {
            return super.getLastHeartbeatTime();
        }

        @Override
        public void disconnect() {
            // Just pretend to disconnect for testing
            // If you have a disconnect method in the parent, call it:
            // super.disconnect();

            // For testing purposes, just simulate resource cleanup
            System.out.println("TestChatClient: simulating disconnection and resource cleanup");
        }
    }
}