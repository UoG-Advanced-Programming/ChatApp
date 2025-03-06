package com.example.models;

import javax.swing.*;
import java.awt.*;

public class ChatListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the parent method to set up the default rendering
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Chat) {
            // Display the name of the Chat object
            Chat chat = (Chat) value;
            setText(chat.getName()); // Use the getName() method of the Chat object
        }

        return this;
    }
}
