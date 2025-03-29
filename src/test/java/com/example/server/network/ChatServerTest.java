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

    private ChatServer chatServer;
    private User user;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private ServerHandler handler;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    @BeforeEach
    void setUp() throws Exception {
        chatServer = new ChatServer();
        user = new User("testUser");
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // Setup sockets
        serverSocket = new ServerSocket(0); // Bind to any available port
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", serverSocket.getLocalPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Socket socket = serverSocket.accept();

        handler = new ServerHandler(socket, chatServer); // Using a real instance with a valid socket
    }

    @Test
    void testAddClient() {
        chatServer.addClient(user, writer, handler);
        System.out.println("Client added: " + user.getUsername());

        assertTrue(chatServer.getClientWriters().containsKey(user));
        assertEquals(writer, chatServer.getClient(user));
    }

    @Test
    void testRemoveClient() {
        chatServer.addClient(user, writer, handler);
        chatServer.removeClient(user);
        System.out.println("Client removed: " + user.getUsername());

        assertFalse(chatServer.getClientWriters().containsKey(user));
    }

    @Test
    void testFindUserById() {
        chatServer.addClient(user, writer, handler);
        Optional<User> foundUser = chatServer.findUserById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        System.out.println("User found by ID: " + user.getId());
    }

    @Test
    void testGetUserSocket() {
        chatServer.addClient(user, writer, handler);

        String actualSocketAddress = chatServer.getUserSocket(user);
        String expectedSocketAddress = clientSocket.getLocalSocketAddress().toString().replaceFirst("/", "");
        assertEquals(expectedSocketAddress, actualSocketAddress);
        System.out.println("User socket address: " + actualSocketAddress);
    }

    @Test
    void testSelectRandomUser() {
        chatServer.addClient(user, writer, handler);
        User randomUser = chatServer.selectRandomUser();

        assertNotNull(randomUser);
        assertEquals(user, randomUser);
        System.out.println("Random user selected: " + randomUser.getUsername());
    }

    @Test
    void testBroadcast() {
        chatServer.addClient(user, writer, handler);
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        chatServer.broadcast(message);
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));
        System.out.println("Broadcast message: " + stringWriter.toString());
    }

    @Test
    void testSend() {
        chatServer.addClient(user, writer, handler);
        Communication message = new SystemMessage(SystemMessageType.HEARTBEAT, "");

        chatServer.send(user, message);
        assertTrue(stringWriter.toString().contains("HEARTBEAT"));
        System.out.println("Sent message: " + stringWriter.toString());
    }

    @Test
    void testShutdown() {
        chatServer.addClient(user, writer, handler);
        chatServer.shutdown();
        System.out.println("Server shutdown initiated.");

        // Allow some time for the shutdown process to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(chatServer.getClientWriters().containsKey(user));
        System.out.println("Server terminated.");
    }
}