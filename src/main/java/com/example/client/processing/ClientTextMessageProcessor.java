package com.example.client.processing;

import com.example.client.gui.Controller;
import com.example.common.chats.Chat;
import com.example.common.chats.PrivateChat;
import com.example.common.messages.Communication;
import com.example.common.messages.TextMessage;

/**
 * The ClientTextMessageProcessor class is responsible for processing
 * text messages on the client side. It extends the ClientMessageProcessor
 * class and handles messages related to text communication.
 */
public class ClientTextMessageProcessor extends ClientMessageProcessor {

    /**
     * Processes a text message and updates the GUI accordingly.
     *
     * @param message    The communication message to process
     * @param controller The controller to update the GUI
     */
    @Override
    public void processMessage(Communication message, Controller controller) {
        TextMessage textMessage = (TextMessage) message; // Cast the message to TextMessage
        System.out.println(textMessage.getSender().getUsername() + ": " + textMessage.getContent()); // Print the sender's username and message content

        Chat chat = textMessage.getChat(); // Get the chat associated with the message

        // Check if the chat is already present in the controller
        if (!controller.hasChat(chat)) {
            controller.addChat(chat); // Add the chat to the controller if not present

            // If the chat is a private chat, set its name to the sender's username
            if (chat instanceof PrivateChat privateChat) {
                privateChat.setName(textMessage.getSender().getUsername());
            }
        }

        controller.showMessage(textMessage); // Display the message in the GUI
    }
}