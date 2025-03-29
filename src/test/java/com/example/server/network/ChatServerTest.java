package com.example.server.network;

import com.example.common.messages.Communication;
import com.example.common.messages.SystemMessage;
import com.example.common.messages.SystemMessageType;
import com.example.common.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChatServerTest {

    // All the stuff we need for testing
    private ChatServer chatServer;
    private User user;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private ServerHandler handler;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    @BeforeEach
    void setUp() throws Exception {
        // Create a server for each test - keeps tests isolated
        chatServer = new ChatServer();
        user = new User("testUser");

        // This captures what the server writes so we can check it
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // Need to set up actual network sockets - bit of a pain but necessary
        serverSocket = new ServerSocket(0); // Just grab any free port

        // Start client socket in separate thread so it doesn't block
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", serverSocket.getLocalPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Wait for the client to connect
        Socket socket = serverSocket.accept();

        // Create a real handler with our test socket
        handler = new ServerHandler(socket, chatServer);
    }

    @Test
    void testAddClient() {
        // Let's add a client and see if it works
        chatServer.addClient(user, writer, handler);
        System.out.println("Client added: " + user.getUsername());

        // Make sure the user got added to the server's client list
        assertTrue(chatServer.getClientWriters().containsKey(user));
        assertEquals(writer, chatServer.getClient(user));
    }

    @Test
    void testRemoveClient() {
        // First add a client
        chatServer.addClient(user, writer, handler);

        // Then try removing them
        chatServer.removeClient(user);
        System.out.println("Client removed: " + user.getUsername());

        // Check they're actually gone
        assertFalse(chatServer.getClientWriters().containsKey(user));
    }

    @Test
    void testFindUserById() {
        // Add a user so we can find them
        chatServer.addClient(user, writer, handler);

        // Try to look them up by ID
        Optional<User> foundUser = chatServer.findUserById(user.getId());

        // Did we find them?
        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        System.out.println("User found by ID: " + user.getId());
    }

    @Test
    void testGetUserSocket() {
        // Add a client to the server
        chatServer.addClient(user, writer, handler);

        // Get their socket address from the server
        String actualSocketAddress = chatServer.getUserSocket(user);

        // Compare with what we expect (strip the leading "/" from local socket address)
        String expectedSocketAddress = clientSocket.getLocalSocketAddress().toString().replaceFirst("/", "");
        assertEquals(expectedSocketAddress, actualSocketAddress);
        System.out.println("User socket address: " + actualSocketAddress);
    }

    @Test
    void testSelectRandomUser() {
        // With only one user, "random" should just give us that user
        chatServer.addClient(user, writer, handler);
        User randomUser = chatServer.selectRandomUser();

        // Make sure we got someone back and it's our test user
        assertNotNull(randomUser);
        assertEquals(user, randomUser);
        System.out.println("Random user selected: " + randomUser.getUsername());
    }

    @Test
    void testBroadcast() {
        // Add a client so they can receive broadcasts
        chatServer.addClient(user, writer, handler);

        // Create a simple heartbeat message
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        // Broadcast to everyone (just our one test user)
        chatServer.broadcast(message);

        // Check our client got the message
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));
        System.out.println("Broadcast message: " + stringWriter.toString());
    }

    @Test
    void testSend() {
        // Add a client so we can send them a message
        chatServer.addClient(user, writer, handler);

        // Create a system message to send
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        // Send directly to our test user
        chatServer.send(user, message);

        // Make sure they got it
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));
        System.out.println("Sent message: " + stringWriter.toString());
    }

    @Test
    void testShutdown() {
        // Add a client first
        chatServer.addClient(user, writer, handler);

        // Now tell the server to shut down
        chatServer.shutdown();
        System.out.println("Server shutdown initiated.");

        // Server shutdown happens in another thread, so give it a sec
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if clients still exist - they should since we're
        // only testing the method call, not full shutdown logic
        assertTrue(chatServer.getClientWriters().containsKey(user));
        System.out.println("Server terminated.");
    }
}