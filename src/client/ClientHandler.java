package client;

import models.ChatType;
import models.Communication;
import models.MessageSerializer;
import models.TextMessage;
import models.UserUpdateMessage;
import models.SystemMessage;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private ClientGUI gui;
    private ChatClient client;

    public ClientHandler(BufferedReader in, ChatClient client) {
        this.in = in;
        this.client = client;
        this.gui = new ClientGUI(this.client);
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage());
        }
    }

    public void processMessage(String jsonMessage) {
        Communication message = MessageSerializer.deserialize(jsonMessage);
        switch (message.getType()) {
            case TEXT:
                TextMessage textMessage = (TextMessage) message;
                // gui.showMessage(textMessage);
                break;

            case USER_UPDATE:
                UserUpdateMessage userUpdateMessage = (UserUpdateMessage) message;
                System.out.println("User " + userUpdateMessage.getUser().getUsername() + " is now " + userUpdateMessage.getStatus());
                break;

            case SYSTEM:
                SystemMessage systemMessage = (SystemMessage) message;
                System.out.println("System Notification: " + systemMessage.getSystemContent());
                break;
        }
    }
}
