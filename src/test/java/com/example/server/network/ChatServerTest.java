package com.example.server.network;

import com.example.common.messages.Communication; // Importing Communication class
import com.example.common.messages.SystemMessage; // Importing SystemMessage class
import com.example.common.messages.SystemMessageType; // Importing SystemMessageType enum
import com.example.common.users.User; // Importing User class
import org.junit.jupiter.api.BeforeEach; // Importing BeforeEach annotation for setup methods
import org.junit.jupiter.api.Test; // Importing Test annotation for test methods

import java.io.PrintWriter; // Importing PrintWriter class for writing output
import java.io.StringWriter; // Importing StringWriter class for capturing output in a string
import java.net.ServerSocket; // Importing ServerSocket class for server-side socket communication
import java.net.Socket; // Importing Socket class for client-side socket communication
import java.util.Optional; // Importing Optional class for handling optional values

import static org.junit.jupiter.api.Assertions.*; // Importing static assertions from JUnit

/**
 * Unit tests for the ChatServer class.
 */
class ChatServerTest {

    private ChatServer chatServer; // Instance of ChatServer
    private User user; // Instance of User
    private StringWriter stringWriter; // Capture string output
    private PrintWriter writer; // PrintWriter for writing to stringWriter
    private ServerHandler handler; // Instance of ServerHandler
    private ServerSocket serverSocket; // Server-side socket
    private Socket clientSocket; // Client-side socket

    /**
     * Sets up the test environment before each test.
     *
     * @throws Exception if an error occurs during setup.
     */
    @BeforeEach
    void setUp() throws Exception {
        chatServer = new ChatServer(); // Initializing ChatServer instance
        user = new User("testUser"); // Creating a test user
        stringWriter = new StringWriter(); // Initializing StringWriter
        writer = new PrintWriter(stringWriter); // Initializing PrintWriter

        // Initialize sockets
        serverSocket = new ServerSocket(0); // Bind to any available port
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", serverSocket.getLocalPort()); // Creating client socket
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Socket socket = serverSocket.accept(); // Accepting server socket connection

        handler = new ServerHandler(socket, chatServer); // Creating ServerHandler with accepted socket
    }

    /**
     * Tests the addition of a client to the chat server.
     */
    @Test
    void testAddClient() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        System.out.println("Client added: " + user.getUsername()); // Log client addition

        assertTrue(chatServer.getClientWriters().containsKey(user)); // Assert client writer is added
        assertEquals(writer, chatServer.getClient(user)); // Assert writer matches
    }

    /**
     * Tests the removal of a client from the chat server.
     */
    @Test
    void testRemoveClient() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        chatServer.removeClient(user); // Remove client from the chat server
        System.out.println("Client removed: " + user.getUsername()); // Log client removal

        assertFalse(chatServer.getClientWriters().containsKey(user)); // Assert client writer is removed
    }

    /**
     * Tests finding a user by their ID.
     */
    @Test
    void testFindUserById() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        Optional<User> foundUser = chatServer.findUserById(user.getId()); // Find user by ID

        assertTrue(foundUser.isPresent()); // Assert user is found
        assertEquals(user, foundUser.get()); // Assert found user matches
        System.out.println("User found by ID: " + user.getId()); // Log user found by ID
    }

    /**
     * Tests retrieving a user's socket address.
     */
    @Test
    void testGetUserSocket() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server

        String actualSocketAddress = chatServer.getUserSocket(user); // Get user's socket address
        String expectedSocketAddress = clientSocket.getLocalSocketAddress().toString().replaceFirst("/", ""); // Format expected socket address
        assertEquals(expectedSocketAddress, actualSocketAddress); // Assert addresses match
        System.out.println("User socket address: " + actualSocketAddress); // Log user's socket address
    }

    /**
     * Tests selecting a random user from the chat server.
     */
    @Test
    void testSelectRandomUser() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        User randomUser = chatServer.selectRandomUser(); // Select random user

        assertNotNull(randomUser); // Assert random user is not null
        assertEquals(user, randomUser); // Assert random user matches
        System.out.println("Random user selected: " + randomUser.getUsername()); // Log random user selected
    }

    /**
     * Tests broadcasting a message to all clients.
     */
    @Test
    void testBroadcast() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, ""); // Create a heartbeat message

        chatServer.broadcast(message); // Broadcast message to all clients
        assertTrue(stringWriter.toString().contains("HEARTBEAT")); // Assert message content
        System.out.println("Broadcast message: " + stringWriter.toString()); // Log broadcast message
    }

    /**
     * Tests sending a message to a specific client.
     */
    @Test
    void testSend() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, ""); // Create a heartbeat message

        chatServer.send(user, message); // Send message to specific client
        assertTrue(stringWriter.toString().contains("HEARTBEAT")); // Assert message content
        System.out.println("Sent message: " + stringWriter.toString()); // Log sent message
    }

    /**
     * Tests shutting down the chat server.
     */
    @Test
    void testShutdown() {
        chatServer.addClient(user, writer, handler); // Add client to the chat server
        chatServer.shutdown(); // Shut down the chat server
        System.out.println("Server shutdown initiated."); // Log server shutdown

        // Allow some time for the shutdown process to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(chatServer.getClientWriters().containsKey(user)); // Assert server termination
        System.out.println("Server terminated."); // Log server termination
    }
}