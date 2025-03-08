package com.example.client.processing;

import com.example.client.gui.ClientGUI;
import com.example.common.messages.Communication;

public abstract class ClientMessageProcessor {
    public abstract void processMessage(Communication message, ClientGUI gui);
}
