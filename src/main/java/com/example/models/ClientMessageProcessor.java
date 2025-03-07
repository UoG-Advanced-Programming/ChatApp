package com.example.models;

import com.example.client.ClientGUI;

public abstract class ClientMessageProcessor {
    public abstract void processMessage(Communication message, ClientGUI gui);
}
