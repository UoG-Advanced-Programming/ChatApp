package com.example.client.gui;

import javax.swing.*;
import java.awt.*;
import com.example.common.users.User;

public class ActiveUserCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        User user = (User) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Append "(Coordinator)" to the name if the user is the coordinator
        if (user.getIsCoordinator()) {
            label.setText(user.getUsername() + " (Coordinator)");
        } else {
            label.setText(user.getUsername());
        }

        // Custom Styling
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Color adjustments
        Color gold = new Color(255, 215, 0);
        Color darkGold = new Color(218, 165, 32); // Darker gold when selected

        if (user.getIsCoordinator()) {
            if (isSelected) {
                label.setBackground(darkGold); // Darker gold when selected
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(gold); // Regular gold for the coordinator
                label.setForeground(Color.BLACK);
            }
        } else if (isSelected) {
            label.setBackground(new Color(30, 144, 255)); // Dodger blue for selection
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.LIGHT_GRAY);
            label.setForeground(Color.BLACK);
        }

        return label;
    }
}
