package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;

public class ClientTextMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ClientGUI gui) {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getSender().getUsername() + ": " + textMessage.getContent());

        Chat chat = textMessage.getChat();
        if (!gui.hasChat(chat)) {
            gui.getChatListModel().addElement(chat);

            if (chat instanceof PrivateChat privateChat) {
                privateChat.setName(textMessage.getSender().getUsername());
            }
        }

        gui.showMessage(textMessage);
    }
}
