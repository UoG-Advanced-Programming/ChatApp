package com.example.server.network;

// Import required classes for server testing
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

/**
 * Unit tests for Server class to validate client management,
 * message handling, and server lifecycle operations.
 */
class ServerTest {

    // Instance of Server under test
    private Server server;

    // Test user used during client operations
    private User user;

    // Writer used to capture output from the server
    private StringWriter stringWriter;

    // PrintWriter wrapping the StringWriter
    private PrintWriter writer;

    // ServerHandler responsible for client connection handling
    private ServerHandler handler;

    // ServerSocket used to simulate server endpoint
    private ServerSocket serverSocket;

    // Socket used to simulate client connection
    private Socket clientSocket;

    /**
     * Initializes test environment before each test case.
     * Sets up server instance, user, sockets, handler, and output writers.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Initialize server instance
        server = new Server();

        // Create a test user named "testUser"
        user = new User("testUser");

        // Set up StringWriter and PrintWriter for output capturing
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // Initialize server socket on an available port
        serverSocket = new ServerSocket(0); // Port 0 means any free port

        // Create a client socket in a separate thread to connect to server
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", serverSocket.getLocalPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Accept client connection on server socket
        Socket socket = serverSocket.accept();

        // Create a ServerHandler with the accepted socket
        handler = new ServerHandler(socket, server);
    }

    /**
     * Verifies that a client is successfully added to the server.
     */
    @Test
    void testAddClient() {
        // Add test client to server
        server.addClient(user, writer, handler);

        // Log client addition
        System.out.println("Client added: " + user.getUsername());

        // Verify client addition
        assertTrue(server.getClientWriters().containsKey(user));
        assertEquals(writer, server.getClient(user));
    }

    /**
     * Verifies that a client is successfully removed from the server.
     */
    @Test
    void testRemoveClient() {
        // Add and remove test client from server
        server.addClient(user, writer, handler);
        server.removeClient(user);

        // Log client removal
        System.out.println("Client removed: " + user.getUsername());

        // Verify client removal
        assertFalse(server.getClientWriters().containsKey(user));
    }

    /**
     * Verifies that a user can be found by their unique identifier.
     */
    @Test
    void testFindUserById() {
        // Add client and attempt to find user by ID
        server.addClient(user, writer, handler);
        Optional<User> foundUser = server.findUserById(user.getId());

        // Verify user lookup
        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());

        // Log user lookup result
        System.out.println("User found by ID: " + user.getId());
    }

    /**
     * Verifies that the correct socket address is retrieved for a user.
     */
    @Test
    void testGetUserSocket() {
        // Add client to server
        server.addClient(user, writer, handler);

        // Retrieve actual socket address
        String actualSocketAddress = server.getUserSocket(user);

        // Expected socket address derived from client socket
        String expectedSocketAddress = clientSocket.getLocalSocketAddress().toString().replaceFirst("/", "");

        // Verify retrieved socket address
        assertEquals(expectedSocketAddress, actualSocketAddress);

        // Log retrieved socket address
        System.out.println("User socket address: " + actualSocketAddress);
    }

    /**
     * Verifies that a random user is selected from the list of connected clients.
     */
    @Test
    void testSelectRandomUser() {
        // Add client to server
        server.addClient(user, writer, handler);

        // Select random user
        User randomUser = server.selectRandomUser();

        // Verify random user selection
        assertNotNull(randomUser);
        assertEquals(user, randomUser);

        // Log random user selection
        System.out.println("Random user selected: " + randomUser.getUsername());
    }

    /**
     * Verifies that a broadcast message is sent to all connected clients.
     */
    @Test
    void testBroadcast() {
        // Add client to server
        server.addClient(user, writer, handler);

        // Create system message to broadcast
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        // Broadcast message to all clients
        server.broadcast(message);

        // Verify that message was broadcasted
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));

        // Log broadcast message
        System.out.println("Broadcast message: " + stringWriter.toString());
    }

    /**
     * Verifies that a message is correctly sent to a specific client.
     */
    @Test
    void testSend() {
        // Add client to server
        server.addClient(user, writer, handler);

        // Create system message to send
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        // Send message to specific user
        server.send(user, message);

        // Verify that message was sent
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));

        // Log sent message
        System.out.println("Sent message: " + stringWriter.toString());
    }

    /**
     * Verifies that the server shutdown procedure is initiated correctly.
     */
    @Test
    void testShutdown() {
        // Add client to server
        server.addClient(user, writer, handler);

        // Initiate server shutdown
        server.shutdown();

        // Log server shutdown
        System.out.println("Server shutdown initiated.");

        // Allow time for shutdown process to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that client data still exists (shutdown process is graceful)
        assertTrue(server.getClientWriters().containsKey(user));

        // Log server termination
        System.out.println("Server terminated.");
    }
}
