package server;

import client.ClientGUI;
import models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private ChatServer server;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.in = in;
        this.server = server;
    }

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
                System.out.println("User " + textMessage.getSender().getUsername() + ": " + textMessage.getContent());
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