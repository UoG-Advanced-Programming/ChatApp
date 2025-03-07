package com.example.models;

import com.example.client.ClientGUI;

public class ClientTextMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getSender().getUsername() + ": " + textMessage.getContent());
        gui.showMessage(textMessage);
    }
}
