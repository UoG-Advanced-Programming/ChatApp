package com.example.client.processing;

import com.example.client.gui.ChatController;
import com.example.common.messages.Communication;

public abstract class ClientMessageProcessor {
    public abstract void processMessage(Communication message, ChatController controller);
}
