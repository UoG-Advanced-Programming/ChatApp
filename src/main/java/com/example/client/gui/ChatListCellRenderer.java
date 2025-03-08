package com.example.client.gui;

import com.example.common.chats.Chat;

import javax.swing.*;
import java.awt.*;

public class ChatListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the parent method to set up the default rendering
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Chat chat) {
            // Display the name of the Chat object
            setText(chat.getName()); // Use the getName() method of the Chat object
        }

        return this;
    }
}
