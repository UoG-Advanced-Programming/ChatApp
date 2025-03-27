package com.example.server.network;

import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.messages.TextMessage;
import com.example.common.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {

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
        // Initialize the chat server
        chatServer = new ChatServer();

        // Create test users
        testUser1 = new User("TestUser1");
        testUser2 = new User("TestUser2");

        // Create a GroupChat with just the name parameter
        testChat = new GroupChat("TestChat");

        // If needed, add the users to the chat after creation
        // (if GroupChat has methods like addUser() or addParticipant())
        testChat.addParticipant(testUser1);  // Adjust method name if needed
        testChat.addParticipant(testUser2);  // Adjust method name if needed

        // Set up StringWriters to capture output
        stringWriter1 = new StringWriter();
        stringWriter2 = new StringWriter();
        printWriter1 = new PrintWriter(stringWriter1, true);
        printWriter2 = new PrintWriter(stringWriter2, true);
    }

    @Test
    public void testAddClient() {
        // Add a client to the server
        chatServer.addClient(testUser1, printWriter1);

        // Verify client was added
        assertTrue(chatServer.getClientWriters().containsKey(testUser1));
        assertEquals(1, chatServer.getClientWriters().size());
    }

    @Test
    public void testRemoveClient() {
        // Add clients
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // Verify they were added
        assertEquals(2, chatServer.getClientWriters().size());

        // Remove a client
        chatServer.removeClient(testUser1);

        // Verify client was removed
        assertFalse(chatServer.getClientWriters().containsKey(testUser1));
        assertTrue(chatServer.getClientWriters().containsKey(testUser2));
        assertEquals(1, chatServer.getClientWriters().size());
    }

    @Test
    public void testBroadcastMessage() {
        // Add clients
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // Create message with the required parameters
        TextMessage message = new TextMessage(testChat, testUser1, "Hello everyone!");

        // Broadcast message
        chatServer.broadcast(message);

        // Verify message was sent to all clients
        assertTrue(stringWriter1.toString().contains("Hello everyone!"));
        assertTrue(stringWriter2.toString().contains("Hello everyone!"));
    }

    @Test
    public void testFirstClientBecomesCoordinator() {
        // Add first client
        chatServer.addClient(testUser1, printWriter1);

        // Verify they're set as coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());

        // Add second client
        chatServer.addClient(testUser2, printWriter2);

        // Verify first client is still coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertFalse(testUser2.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());
    }

    @Test
    public void testCoordinatorReassignmentWhenCoordinatorLeaves() {
        // Add clients
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // First client should be coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());

        // Remove coordinator
        chatServer.removeClient(testUser1);

        // Second client should now be coordinator
        assertTrue(testUser2.getIsCoordinator());
        assertEquals(testUser2, chatServer.getCoordinator());
    }
}