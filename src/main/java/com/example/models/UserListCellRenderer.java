package com.example.models;

import javax.swing.*;
import java.awt.*;

public class UserListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the parent method to set up the default rendering
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof User) {
            // Display the username of the User object
            User user = (User) value;
            setText(user.getUsername()); // Use the getUsername() method of the User object
        }

        return this;
    }
}
