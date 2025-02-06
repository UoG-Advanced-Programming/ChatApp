package Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Server.Server;
import Server.ClientHandler;
import java.net.Socket;

public class ServerTest {
    @Test
    public void testCoordinatorElection() {
        Server server = new Server();
        Socket mockSocket = new Socket();
        ClientHandler client1 = new ClientHandler(mockSocket, "Client-1");
        ClientHandler client2 = new ClientHandler(mockSocket, "Client-2");

        server.addClient(client1);
        server.addClient(client2);

        assertEquals("Client-1", Server.getCoordinatorID(), "First client should be the coordinator.");
    }

    @Test
    public void testBroadcastMessage() {
        // Test broadcasting messages
    }

    @Test
    public void testPrivateMessage() {
        // Test private messaging
    }
}