package com.example.models;

import javax.swing.*;
import java.awt.*;

public class UserListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the parent method to set up the default rendering
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof User user) {
            // Display the username of the User object
            setText(user.getUsername()); // Use the getUsername() method of the User object
        }

        return this;
    }
}
