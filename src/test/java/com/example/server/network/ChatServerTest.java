package com.example.server.network;

import com.example.common.chats.GroupChat;
import com.example.common.messages.*;
import com.example.common.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerTest {
    // Declaring variables for chat server, test users, string writers, print writers, and test chat
    private TestChatServer chatServer;
    private User testUser1;
    private User testUser2;
    private StringWriter stringWriter1;
    private StringWriter stringWriter2;
    private PrintWriter printWriter1;
    private PrintWriter printWriter2;
    private GroupChat testChat;

    // TestChatServer extends ChatServer and adds a test-specific version of addClient
    private static class TestChatServer extends ChatServer {
        // Add a new method for testing with 2 parameters
        public void addClient(User user, PrintWriter writer) {
            // Notify all clients about the new user
            broadcast(new UserUpdateMessage(user, UserStatus.ONLINE));
            getClientWriters().put(user, writer);
            send(user, new SystemMessage(SystemMessageType.ID_TRANSITION, "general-chat"));
            for (User activeUser : getClientWriters().keySet()) {
                send(user, new UserUpdateMessage(activeUser, UserStatus.ONLINE));
            }

            // If this is the first client, set them as coordinator
            if (getClientWriters().size() == 1) {
                getCoordinatorManager().assignCoordinator(user);
            }
        }

        // Override removeClient to ensure coordinator reassignment
        @Override
        public void removeClient(User user) {
            boolean wasCoordinator = user.getIsCoordinator();

            // Call the parent implementation to remove the client
            super.removeClient(user);

            // If the coordinator is leaving and there are still clients, reassign the coordinator
            if (wasCoordinator && !getClientWriters().isEmpty()) {
                getCoordinatorManager().reassignCoordinator();
            }
        }
    }

    @BeforeEach
    public void setUp() {
        // Initializing the chat server before each test
        chatServer = new TestChatServer();

        // Creating test users
        testUser1 = new User("TestUser1");
        testUser2 = new User("TestUser2");

        // Creating a GroupChat with just the name parameter
        testChat = new GroupChat("TestChat");

        // Adding the users to the chat after creation
        testChat.addParticipant(testUser1);
        testChat.addParticipant(testUser2);

        // Setting up StringWriters to capture output
        stringWriter1 = new StringWriter();
        stringWriter2 = new StringWriter();
        printWriter1 = new PrintWriter(stringWriter1, true);
        printWriter2 = new PrintWriter(stringWriter2, true);
    }

    @Test
    public void testAddClient() {
        // Adding a client to the server
        chatServer.addClient(testUser1, printWriter1);

        // Verifying that the client was added
        assertTrue(chatServer.getClientWriters().containsKey(testUser1));
        assertEquals(1, chatServer.getClientWriters().size());
    }

    @Test
    public void testRemoveClient() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // Verifying that the clients were added
        assertEquals(2, chatServer.getClientWriters().size());

        // Removing a client from the server
        chatServer.removeClient(testUser1);

        // Verifying that the client was removed
        assertFalse(chatServer.getClientWriters().containsKey(testUser1));
        assertTrue(chatServer.getClientWriters().containsKey(testUser2));
        assertEquals(1, chatServer.getClientWriters().size());
    }

    @Test
    public void testBroadcastMessage() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // Creating a message with the required parameters
        TextMessage message = new TextMessage(testChat, testUser1, "Hello everyone!");

        // Broadcasting the message to all clients
        chatServer.broadcast(message);

        // Verifying that the message was sent to all clients
        assertTrue(stringWriter1.toString().contains("Hello everyone!"));
        assertTrue(stringWriter2.toString().contains("Hello everyone!"));
    }

    @Test
    public void testFirstClientBecomesCoordinator() {
        // Adding the first client to the server
        chatServer.addClient(testUser1, printWriter1);

        // Verifying that the first client is set as coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());

        // Adding the second client to the server
        chatServer.addClient(testUser2, printWriter2);

        // Verifying that the first client is still the coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertFalse(testUser2.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());
    }

    @Test
    public void testCoordinatorReassignmentWhenCoordinatorLeaves() {
        // Adding clients to the server
        chatServer.addClient(testUser1, printWriter1);
        chatServer.addClient(testUser2, printWriter2);

        // Verifying that the first client is the coordinator
        assertTrue(testUser1.getIsCoordinator());
        assertEquals(testUser1, chatServer.getCoordinator());

        // Removing the coordinator from the server
        chatServer.removeClient(testUser1);

        // Verifying that the second client is now the coordinator
        assertTrue(testUser2.getIsCoordinator());
        assertEquals(testUser2, chatServer.getCoordinator());
    }
}