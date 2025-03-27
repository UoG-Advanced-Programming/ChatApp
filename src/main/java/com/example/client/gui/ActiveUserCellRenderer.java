package com.example.client.gui;

import javax.swing.*;
import java.awt.*;
import com.example.common.users.User;

public class ActiveUserCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Cast value to User
        User user = (User) value;

        // Create a label for each user
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(user.getUsername()); // Display username

        // Custom Styling
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding

        // Background color for selected item
        if (isSelected) {
            label.setBackground(new Color(30, 144, 255)); // Dodger blue
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.LIGHT_GRAY);
            label.setForeground(Color.BLACK);
        }

        return label;
    }
}
