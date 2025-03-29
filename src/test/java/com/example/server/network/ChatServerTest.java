package com.example.server.network;

// Import statements for various classes used in the tests
import com.example.common.chats.GroupChat;
import com.example.common.messages.TextMessage;
import com.example.common.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {
    // Declaring variables for chat server, test users, string writers, print writers, and test chat
    private ChatServer chatServer;
    private User testUser1;
    private User testUser2;
    private StringWriter stringWriter1;
    private StringWriter stringWriter2;
    private PrintWriter printWriter1;
    private PrintWriter printWriter2;
    private GroupChat testChat;

    @BeforeEach
    public void setUp() {
        // Initializing the chat server before each test
        chatServer = new ChatServer(); // Create a new instance of ChatServer

        // Creating test users
        testUser1 = new User("TestUser1"); // Create a new user named "TestUser1"
        testUser2 = new User("TestUser2"); // Create a new user named "TestUser2"

        // Creating a GroupChat with just the name parameter
        testChat = new GroupChat("TestChat"); // Create a new group chat named "TestChat"

        // Adding the users to the chat after creation
        // (if GroupChat has methods like addUser() or addParticipant())
        testChat.addParticipant(testUser1);  // Add testUser1 to the group chat
        testChat.addParticipant(testUser2);  // Add testUser2 to the group chat

        // Setting up StringWriters to capture output
        stringWriter1 = new StringWriter(); // Initialize StringWriter for testUser1
        stringWriter2 = new StringWriter(); // Initialize StringWriter for testUser2
        printWriter1 = new PrintWriter(stringWriter1, true); // Wrap StringWriter with PrintWriter for testUser1
        printWriter2 = new PrintWriter(stringWriter2, true); // Wrap StringWriter with PrintWriter for testUser2
    }

    @Test
    public void testAddClient() {
        // Adding a client to the server
        chatServer.addClient(testUser1, printWriter1); // Add testUser1 with printWriter1 to the server

        // Verifying that the client was added
        assertTrue(chatServer.getClientWriters().containsKey(testUser1)); // Check if testUser1 is in the client writers map
        assertEquals(1, chatServer.getClientWriters().size()); // Check if the size of the client writers map is 1
    }

    @Test
    public void testRemoveClient() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1); // Add testUser1 with printWriter1 to the server
        chatServer.addClient(testUser2, printWriter2); // Add testUser2 with printWriter2 to the server

        // Verifying that the clients were added
        assertEquals(2, chatServer.getClientWriters().size()); // Check if the size of the client writers map is 2

        // Removing a client from the server
        chatServer.removeClient(testUser1); // Remove testUser1 from the server

        // Verifying that the client was removed
        assertFalse(chatServer.getClientWriters().containsKey(testUser1)); // Check if testUser1 is not in the client writers map
        assertTrue(chatServer.getClientWriters().containsKey(testUser2)); // Check if testUser2 is still in the client writers map
        assertEquals(1, chatServer.getClientWriters().size()); // Check if the size of the client writers map is 1
    }

    @Test
    public void testBroadcastMessage() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1); // Add testUser1 with printWriter1 to the server
        chatServer.addClient(testUser2, printWriter2); // Add testUser2 with printWriter2 to the server

        // Creating a message with the required parameters
        TextMessage message = new TextMessage(testChat, testUser1, "Hello everyone!"); // Create a new text message

        // Broadcasting the message to all clients
        chatServer.broadcast(message); // Broadcast the message

        // Verifying that the message was sent to all clients
        assertTrue(stringWriter1.toString().contains("Hello everyone!")); // Check if the message content is in stringWriter1
        assertTrue(stringWriter2.toString().contains("Hello everyone!")); // Check if the message content is in stringWriter2
    }

    @Test
    public void testFirstClientBecomesCoordinator() {
        // Adding the first client to the server
        chatServer.addClient(testUser1, printWriter1); // Add testUser1 with printWriter1 to the server

        // Verifying that the first client is set as coordinator
        assertTrue(testUser1.getIsCoordinator()); // Check if testUser1 is the coordinator
        assertEquals(testUser1, chatServer.getCoordinator()); // Check if testUser1 is the coordinator in the server

        // Adding the second client to the server
        chatServer.addClient(testUser2, printWriter2); // Add testUser2 with printWriter2 to the server

        // Verifying that the first client is still the coordinator
        assertTrue(testUser1.getIsCoordinator()); // Check if testUser1 is still the coordinator
        assertFalse(testUser2.getIsCoordinator()); // Check if testUser2 is not the coordinator
        assertEquals(testUser1, chatServer.getCoordinator()); // Check if testUser1 is still the coordinator in the server
    }

    @Test
    public void testCoordinatorReassignmentWhenCoordinatorLeaves() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1); // Add testUser1 with printWriter1 to the server
        chatServer.addClient(testUser2, printWriter2); // Add testUser2 with printWriter2 to the server

        // Verifying that the first client is the coordinator
        assertTrue(testUser1.getIsCoordinator()); // Check if testUser1 is the coordinator
        assertEquals(testUser1, chatServer.getCoordinator()); // Check if testUser1 is the coordinator in the server

        // Removing the coordinator from the server
        chatServer.removeClient(testUser1); // Remove testUser1 from the server

        // Verifying that the second client is now the coordinator
        assertTrue(testUser2.getIsCoordinator()); // Check if testUser2 is now the coordinator
        assertEquals(testUser2, chatServer.getCoordinator()); // Check if testUser2 is the coordinator in the server
    }
}