package com.example.client.processing;

import com.example.client.gui.Controller;
import com.example.common.messages.Communication;

/**
 * The ClientMessageProcessor class is an abstract base class for processing
 * different types of messages on the client side.
 */
public abstract class ClientMessageProcessor {

    /**
     * Processes a communication message and updates the GUI accordingly.
     * This method must be implemented by subclasses.
     *
     * @param message    The communication message to process
     * @param controller The controller to update the GUI
     */
    public abstract void processMessage(Communication message, Controller controller);
}