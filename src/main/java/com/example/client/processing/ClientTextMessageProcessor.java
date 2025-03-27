package com.example.client.processing;

import com.example.client.gui.ChatController;
import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;

public class ClientTextMessageProcessor extends ClientMessageProcessor {
    @Override
    public void processMessage(Communication message, ChatController controller) {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getSender().getUsername() + ": " + textMessage.getContent());

        Chat chat = textMessage.getChat();

        if (!controller.hasChat(chat)) {
            controller.addChat(chat);

            if (chat instanceof PrivateChat privateChat) {
                privateChat.setName(textMessage.getSender().getUsername());
            }
        }

        controller.showMessage(textMessage);
    }
}
