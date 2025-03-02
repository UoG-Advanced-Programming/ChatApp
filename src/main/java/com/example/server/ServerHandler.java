package com.example.server;

import com.example.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;

    public ServerHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing input stream: " + e.getMessage());
        }
    }

    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error in communication: " + e.getMessage());
        } finally {
            try {
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
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