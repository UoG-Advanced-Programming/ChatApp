package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;

public class ClientTextMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getSender().getUsername() + ": " + textMessage.getContent());
        gui.showMessage(textMessage);
    }
}
