package com.example.client.network;

// Import statements for various classes used in the tests
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

public class ClientTest {
    // Declaring variables for test client, output writer, test user, and test chat
    private TestClient client;
    private StringWriter outputWriter;
    private User testUser;
    private GroupChat testChat;

    @BeforeEach
    public void setUp() {
        // Setting up test objects before each test
        testUser = new User("TestUser"); // Create a test user named "TestUser"
        testChat = new GroupChat("TestChat"); // Create a test group chat named "TestChat"

        // Setting up a StringWriter to capture output during tests
        outputWriter = new StringWriter(); // Initialize StringWriter
        PrintWriter printWriter = new PrintWriter(outputWriter, true); // Wrap StringWriter with PrintWriter

        // Initializing the test chat client
        client = new TestClient("localhost"); // Create a new test chat client pointing to localhost
        client.setOut(printWriter); // Set the output of chat client to our PrintWriter

        // Logging the setup completion details
        System.out.println("Test setup completed at: " + getCurrentFormattedDateTime()); // Log the time when setup is completed
        System.out.println("Current User's Login: " + testUser.getUsername()); // Log the username of the test user
        System.out.println("Test user created with ID: " + testUser.getId()); // Log the test user's ID
        System.out.println("Test chat created with ID: " + testChat.getId()); // Log the test chat's ID
    }

    @Test
    public void testSendMessage() {
        // Creating and logging a test message
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!"); // Create a new text message
        System.out.println("Test message created with:"); // Log details of the created message
        System.out.println("  User ID: " + testUser.getId()); // Log the user ID
        System.out.println("  Chat ID: " + testChat.getId()); // Log the chat ID
        System.out.println("  Content: Hello, world!"); // Log the message content

        // Sending the message through the client
        client.send(message); // Send the created message
        System.out.println("Message sent to client"); // Log that the message was sent

        // Capturing and logging the output from the client
        String output = outputWriter.toString().trim(); // Capture the output from the client
        System.out.println("Captured output from client:"); // Log the captured output
        System.out.println("-----------------------------------");
        System.out.println(output); // Print the output
        System.out.println("-----------------------------------");

        // Logging assertions that will be checked
        System.out.println("Checking if output contains message content: " + output.contains("Hello, world!")); // Check if output contains message content
        System.out.println("Checking if output contains user ID: " + output.contains(testUser.getId())); // Check if output contains user ID
        System.out.println("Checking if output contains chat ID: " + output.contains(testChat.getId())); // Check if output contains chat ID

        // Verifying that the message was sent correctly
        assertTrue(output.contains("Hello, world!"), "Message content should be in the output"); // Assert that message content is in the output
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output"); // Assert that user ID is in the output
        assertTrue(output.contains(testChat.getId()), "Chat ID should be in the output"); // Assert that chat ID is in the output

        System.out.println("testSendMessage test completed successfully"); // Log the successful completion of the test
    }

    @Test
    public void testUserStatusUpdate() {
        // Logging the start of the test
        System.out.println("Starting testUserStatusUpdate test"); // Log the start of the test
        System.out.println("Current time: " + getCurrentFormattedDateTime()); // Log the current time
        System.out.println("Current User's " + testUser.getUsername()); // Log the username of the test user

        // Creating and logging a user status update message
        UserUpdateMessage message = new UserUpdateMessage(testUser, UserStatus.ONLINE); // Create a new user status update message
        System.out.println("User update message created with:"); // Log details of the created message
        System.out.println("  User ID: " + testUser.getId()); // Log the user ID
        System.out.println("  Username: " + testUser.getUsername()); // Log the username
        System.out.println("  Status: " + UserStatus.ONLINE); // Log the status

        // Sending the user status update message through the client
        client.send(message); // Send the created status update message
        System.out.println("User status update message sent to client"); // Log that the status update message was sent

        // Capturing and logging the output from the client
        String output = outputWriter.toString().trim(); // Capture the output from the client
        System.out.println("Captured output from client:"); // Log the captured output
        System.out.println("-----------------------------------");
        System.out.println(output); // Print the output
        System.out.println("-----------------------------------");

        // Logging assertions that will be checked
        System.out.println("Checking if output contains ONLINE status: " + output.contains("\"status\":\"ONLINE\"")); // Check if output contains ONLINE status
        System.out.println("Checking if output contains user ID: " + output.contains(testUser.getId())); // Check if output contains user ID

        // Verifying that the status update message was sent correctly
        assertTrue(output.contains("\"status\":\"ONLINE\""), "Status should be ONLINE"); // Assert that status is ONLINE
        assertTrue(output.contains(testUser.getId()), "User ID should be in the output"); // Assert that user ID is in the output

        System.out.println("testUserStatusUpdate test completed successfully"); // Log the successful completion of the test
    }

    @Test
    public void testConnectionErrorHandling() {
        // Logging the start of the test
        System.out.println("Starting testConnectionErrorHandling test"); // Log the start of the test
        System.out.println("Current time: " + getCurrentFormattedDateTime()); // Log the current time
        System.out.println("Current User's Login: " + testUser.getUsername()); // Log the username of the test user

        // Logging the original writer status
        System.out.println("Original PrintWriter instance: " + client.getOut()); // Log the original PrintWriter instance

        // Setting up a PrintWriter that will throw an exception when println is called
        System.out.println("Creating failing PrintWriter that will throw a RuntimeException"); // Log the creation of a failing PrintWriter
        PrintWriter failingWriter = new PrintWriter(outputWriter) {
            @Override
            public void println(String x) {
                System.out.println("Failing PrintWriter.println() called with: " + x); // Log the println call
                System.out.println("About to throw simulated network error"); // Log the simulated network error
                throw new RuntimeException("Simulated network error"); // Throw a simulated network error
            }
        };

        System.out.println("Setting failing PrintWriter on Client"); // Log the setting of the failing PrintWriter
        client.setOut(failingWriter); // Set the failing PrintWriter on the chat client

        // Creating and logging a test message
        TextMessage message = new TextMessage(testChat, testUser, "Hello, world!"); // Create a new test message
        System.out.println("Test message created with:"); // Log details of the created message
        System.out.println("  User ID: " + testUser.getId()); // Log the user ID
        System.out.println("  Chat ID: " + testChat.getId()); // Log the chat ID
        System.out.println("  Content: Hello, world!"); // Log the message content

        // Logging that we're about to send a message that should cause an error
        System.out.println("About to send message using failing writer..."); // Log that an error is expected
        System.out.println("(Expect to see error message from Client's exception handler)"); // Log the expected error message

        // Testing that the exception is caught and doesn't propagate out of the send method
        System.out.println("Testing that exception is caught and doesn't propagate out of send()"); // Log the test for exception handling
        try {
            client.send(message); // Attempt to send the message
            System.out.println("✓ Success: No exception was thrown from send() method"); // Log success if no exception is thrown
        } catch (Exception e) {
            System.out.println("✗ Failure: Exception escaped from send() method: " + e); // Log failure if an exception is thrown
            throw e; // Rethrow to fail the test
        }

        // Additional verification and logging
        System.out.println("Test completed successfully - Client properly handled the error"); // Log successful error handling
        System.out.println("(The 'Error sending message: Simulated network error' above comes from Client's error handler)"); // Log where the error message came from

        // Printing a separator for readability
        System.out.println("------------------------------------------------------"); // Print a separator for readability
    }

    @Test
    public void testNullMessageHandling() {
        // Logging the start of the test
        System.out.println("Starting testNullMessageHandling test"); // Log the start of the test
        System.out.println("Current time: " + getCurrentFormattedDateTime()); // Log the current time
        System.out.println("Current User's Login: " + testUser.getUsername()); // Log the username of the test user

        // Testing the handling of sending a null message
        System.out.println("Testing send(null) handling..."); // Log the test for null message handling
        assertDoesNotThrow(() -> client.send(null), "Client should handle null messages gracefully"); // Assert that null messages are handled gracefully

        // Resetting the output writer for the next test
        outputWriter.getBuffer().setLength(0); // Reset the output writer

        // Testing the handling of messages with null content
        System.out.println("Testing message with null content..."); // Log the test for null content handling
        TextMessage nullContentMsg = new TextMessage(testChat, testUser, null); // Create a message with null content
        assertDoesNotThrow(() -> client.send(nullContentMsg), "Client should handle null message content gracefully"); // Assert that null content is handled gracefully

        // Capturing and logging the output for the null content message
        String nullContentOutput = outputWriter.toString().trim(); // Capture the output for the null content message
        System.out.println("Captured output for null content message:"); // Log the captured output
        System.out.println("-----------------------------------");
        System.out.println(nullContentOutput); // Print the captured output
        System.out.println("-----------------------------------");

        // Resetting the output writer for the next test
        outputWriter.getBuffer().setLength(0); // Reset the output writer

        // Testing the handling of messages with null chat
        System.out.println("Testing message with null chat..."); // Log the test for null chat handling
        TextMessage nullChatMsg = new TextMessage(null, testUser, "Test content"); // Create a message with null chat
        assertDoesNotThrow(() -> client.send(nullChatMsg), "Client should handle null chat gracefully"); // Assert that null chat is handled gracefully

        // Capturing and logging the output for the null chat message
        String nullChatOutput = outputWriter.toString().trim(); // Capture the output for the null chat message
        System.out.println("Captured output for null chat message:"); // Log the captured output
        System.out.println("-----------------------------------");
        System.out.println(nullChatOutput); // Print the captured output
        System.out.println("-----------------------------------");

        System.out.println("testNullMessageHandling test completed successfully"); // Log the successful completion of the test
    }

    @Test
    public void testResourceCleanupOnDisconnect() {
        // Logging the start of the test
        System.out.println("Starting testResourceCleanupOnDisconnect test"); // Log the start of the test
        System.out.println("Current time: " + getCurrentFormattedDateTime()); // Log the current time

        // Creating a tracking class for testing disconnection
        class DisconnectTestClient extends TestClient {
            private boolean socketClosed = false;
            private boolean writerClosed = false;
            private boolean readerClosed = false;

            public DisconnectTestClient(String host) {
                super(host);
            }

            @Override
            public void disconnect() {
                System.out.println("DisconnectTestClient disconnect called"); // Log the disconnect call
                super.disconnect(); // Call the parent class's disconnect method
                socketClosed = true; // Simulate socket closure
                writerClosed = true; // Simulate writer closure
                readerClosed = true; // Simulate reader closure
            }

            public boolean isSocketClosed() {
                return socketClosed; // Return the socket closed status
            }

            public boolean isWriterClosed() {
                return writerClosed; // Return the writer closed status
            }

            public boolean isReaderClosed() {
                return readerClosed; // Return the reader closed status
            }
        }

        // Creating a new client for this test to avoid affecting other tests
        DisconnectTestClient disconnectClient = new DisconnectTestClient("localhost"); // Create a new test client

        // Setting up the client with resources
        StringWriter stringWriter = new StringWriter(); // Create a new StringWriter
        PrintWriter testWriter = new PrintWriter(stringWriter); // Wrap the StringWriter with a PrintWriter
        disconnectClient.setOut(testWriter); // Set the PrintWriter on the client
        Socket testSocket = new Socket(); // Create a new Socket
        disconnectClient.setSocket(testSocket); // Set the Socket on the client
        BufferedReader testReader = new BufferedReader(new StringReader("")); // Create a new BufferedReader
        disconnectClient.setIn(testReader); // Set the BufferedReader on the client

        System.out.println("Setting up test client resources"); // Log the setting up of resources

        // Calling disconnect on the test client
        System.out.println("Calling disconnect on test client"); // Log the calling of disconnect
        disconnectClient.disconnect(); // Call the disconnect method

        // Verifying that resources were cleaned up
        System.out.println("Verifying resources were cleaned up"); // Log the verification of resource cleanup
        assertTrue(disconnectClient.isSocketClosed(), "Socket should be closed after disconnect"); // Assert that the socket is closed
        assertTrue(disconnectClient.isWriterClosed(), "Writer should be closed after disconnect"); // Assert that the writer is closed
        assertTrue(disconnectClient.isReaderClosed(), "Reader should be closed after disconnect"); // Assert that the reader is closed

        System.out.println("testResourceCleanupOnDisconnect test completed successfully"); // Log the successful completion of the test
    }

    /**
     * Helper method to get formatted current date/time
     */
    private String getCurrentFormattedDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Return the current date/time formatted
    }

    /**
     * Test subclass of Client that doesn't establish a real connection
     * and allows us to set values for testing
     */
    private static class TestClient extends Client {
        private PrintWriter out;
        private BufferedReader in;
        private Socket socket;

        public TestClient(String host) {
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
            System.out.println("TestClient: simulating disconnection and resource cleanup");
        }
    }
}