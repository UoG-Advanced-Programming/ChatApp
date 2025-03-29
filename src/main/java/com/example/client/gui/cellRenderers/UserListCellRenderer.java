package com.example.client.gui.cellRenderers;

import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;

public class UserListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof User user) {
            label.setText(user.getUsername()); // Display the username
        }

        // Customize appearance
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10)); // Padding
        label.setOpaque(true);
        label.setForeground(Color.WHITE);

        if (isSelected) {
            label.setBackground(new Color(30, 144, 255)); // Dodger Blue for selection
        } else {
            label.setBackground(new Color(50, 50, 50)); // Dark gray background
        }

        return label;
    }
}
