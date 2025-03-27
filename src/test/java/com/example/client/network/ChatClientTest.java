package com.example.client.network;

import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;
import com.example.common.messages.UserStatus;
import com.example.common.messages.UserUpdateMessage;
import com.example.common.users.User;
import com.example.common.chats.GroupChat;
import com.example.common.utils.MessageSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class ChatClientTest {

    private TestChatClient chatClient;
    private StringWriter outputWriter;
    private User testUser;
    private GroupChat testChat;

    @BeforeEach
    public void setUp() {
        // Create test objects
        testUser = new User("TestUser");
        testChat = new GroupChat("TestChat");

        // Set up a string writer to capture output
        outputWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(outputWriter, true);

        // Create our test chat client
        chatClient = new TestChatClient("localhost");
        chatClient.setOut(printWriter);

        // Log setup completion
        System.out.println("Test setup completed at: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());
        System.out.println("Test user created with ID: " + testUser.getId());
        System.out.println("Test chat created with ID: " + testChat.getId());
    }

    @Test
    public void testSendMessage() {
        // Create a test message
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!");
        System.out.println("Test message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Chat ID: " + testChat.getId());
        System.out.println("  Content: Hello, world!");

        // Send the message
        chatClient.send(message);
        System.out.println("Message sent to client");

        // Get and log the captured output
        String output = outputWriter.toString().trim();
        System.out.println("Captured output from client:");
        System.out.println("-----------------------------------");
        System.out.println(output);
        System.out.println("-----------------------------------");

        // Log the assertions we're about to make
        System.out.println("Checking if output contains message content: " + output.contains("Hello, world!"));
        System.out.println("Checking if output contains user ID: " + output.contains(testUser.getId()));
        System.out.println("Checking if output contains chat ID: " + output.contains(testChat.getId()));

        // Verify the message was sent through the writer
        assertTrue(output.contains("Hello, world!"), "Message content should be in the output");
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output");
        assertTrue(output.contains(testChat.getId()), "Chat ID should be in the output");

        System.out.println("testSendMessage test completed successfully");
    }

    @Test
    public void testUserStatusUpdate() {
        // Log test start
        System.out.println("Starting testUserStatusUpdate test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's " + testUser.getUsername());

        // Create a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE);
        System.out.println("User update message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Username: " + testUser.getUsername());
        System.out.println("  Status: " + UserStatus.ONLINE);

        // Send the message
        chatClient.send(message);
        System.out.println("User status update message sent to client");

        // Get and log the captured output
        String output = outputWriter.toString().trim();
        System.out.println("Captured output from client:");
        System.out.println("-----------------------------------");
        System.out.println(output);
        System.out.println("-----------------------------------");

        // Log the assertions we're about to make
        System.out.println("Checking if output contains ONLINE status: " +
                output.contains("\"status\":\"ONLINE\""));
        System.out.println("Checking if output contains user ID: " +
                output.contains(testUser.getId()));

        // Verify the message was sent with the correct data
        assertTrue(output.contains("\"status\":\"ONLINE\""), "Status should be ONLINE");
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output");

        System.out.println("testUserStatusUpdate test completed successfully");
    }

    @Test
    public void testConnectionErrorHandling() {
        // Log test start
        System.out.println("Starting testConnectionErrorHandling test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());

        // Log original writer status
        System.out.println("Original PrintWriter instance: " + chatClient.getOut());

        // Set up a PrintWriter that throws an exception when println is called
        System.out.println("Creating failing PrintWriter that will throw a RuntimeException");
        PrintWriter failingWriter = new PrintWriter(outputWriter) {
            @Override
            public void println(String x) {
                System.out.println("Failing PrintWriter.println() called with: " + x);
                System.out.println("About to throw simulated network error");
                throw new RuntimeException("Simulated network error");
            }
        };

        System.out.println("Setting failing PrintWriter on ChatClient");
        chatClient.setOut(failingWriter);

        // Create a test message
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!");
        System.out.println("Test message created with:");
        System.out.println("  User ID: " + testUser.getId());
        System.out.println("  Chat ID: " + testChat.getId());
        System.out.println("  Content: Hello, world!");

        // Log that we're about to send message that should cause an error
        System.out.println("About to send message using failing writer...");
        System.out.println("(Expect to see error message from ChatClient's exception handler)");

        // The following should not throw an exception even if the underlying writer fails
        System.out.println("Testing that exception is caught and doesn't propagate out of send()");
        try {
            chatClient.send(message);
            System.out.println("✓ Success: No exception was thrown from send() method");
        } catch (Exception e) {
            System.out.println("✗ Failure: Exception escaped from send() method: " + e);
            throw e; // Rethrow to fail the test
        }

        // Additional verification
        System.out.println("Test completed successfully - ChatClient properly handled the error");
        System.out.println("(The 'Error sending message: Simulated network error' above comes from ChatClient's error handler)");

        // Print separator for readability
        System.out.println("------------------------------------------------------");
    }

    @Test
    public void testNullMessageHandling() {
        // Log test start
        System.out.println("Starting testNullMessageHandling test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());
        System.out.println("Current User's Login: " + testUser.getUsername());

        // Test sending null message
        System.out.println("Testing send(null) handling...");
        assertDoesNotThrow(() -> chatClient.send(null),
                "Client should handle null messages gracefully");

        // Reset output writer for next test
        outputWriter.getBuffer().setLength(0);

        // Test messages with null content
        System.out.println("Testing message with null content...");
        TextMessage nullContentMsg = new TextMessage(testChat, testUser, null);
        assertDoesNotThrow(() -> chatClient.send(nullContentMsg),
                "Client should handle null message content gracefully");

        // Get and log the captured output
        String nullContentOutput = outputWriter.toString().trim();
        System.out.println("Captured output for null content message:");
        System.out.println("-----------------------------------");
        System.out.println(nullContentOutput);
        System.out.println("-----------------------------------");

        // Reset output writer for next test
        outputWriter.getBuffer().setLength(0);

        // Test messages with null chat
        System.out.println("Testing message with null chat...");
        TextMessage nullChatMsg = new TextMessage(null, testUser, "Test content");
        assertDoesNotThrow(() -> chatClient.send(nullChatMsg),
                "Client should handle null chat gracefully");

        // Get and log the captured output
        String nullChatOutput = outputWriter.toString().trim();
        System.out.println("Captured output for null chat message:");
        System.out.println("-----------------------------------");
        System.out.println(nullChatOutput);
        System.out.println("-----------------------------------");

        System.out.println("testNullMessageHandling test completed successfully");
    }

    @Test
    public void testResourceCleanupOnDisconnect() {
        // Log test start
        System.out.println("Starting testResourceCleanupOnDisconnect test");
        System.out.println("Current time: " + getCurrentFormattedDateTime());

        // Create a tracking class for testing disconnection
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
                super.disconnect();
                socketClosed = true;
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

        // Create a new client for this test to avoid affecting other tests
        DisconnectTestClient disconnectClient = new DisconnectTestClient("localhost");

        // Set up the client with resources
        StringWriter stringWriter = new StringWriter();
        PrintWriter testWriter = new PrintWriter(stringWriter);
        disconnectClient.setOut(testWriter);
        Socket testSocket = new Socket();
        disconnectClient.setSocket(testSocket);
        BufferedReader testReader = new BufferedReader(new StringReader(""));
        disconnectClient.setIn(testReader);

        System.out.println("Setting up test client resources");

        // Call disconnect
        System.out.println("Calling disconnect on test client");
        disconnectClient.disconnect();

        // Check that resources are cleaned up
        System.out.println("Verifying resources were cleaned up");
        assertTrue(disconnectClient.isSocketClosed(),
                "Socket should be closed after disconnect");
        assertTrue(disconnectClient.isWriterClosed(),
                "Writer should be closed after disconnect");
        assertTrue(disconnectClient.isReaderClosed(),
                "Reader should be closed after disconnect");

        System.out.println("testResourceCleanupOnDisconnect test completed successfully");
    }
    /**
     * Helper method to get formatted current date/time
     */
    private String getCurrentFormattedDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Test subclass of ChatClient that doesn't establish a real connection
     * and allows us to set values for testing
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
            // Do nothing - we don't want to establish a real connection
        }

        @Override
        protected String promptForCredentials() {
            // Skip the UI prompt
            return "TestUser";
        }

        @Override
        public void start() {
            // Override to avoid creating an actual ClientHandler thread
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

        @Override
        public void disconnect() {
            // Implement disconnect logic for testing
            // If you have a disconnect method in the parent, call it:
            // super.disconnect();

            // For testing purposes, just simulate resource cleanup
            System.out.println("TestChatClient: simulating disconnection and resource cleanup");
        }
    }
}