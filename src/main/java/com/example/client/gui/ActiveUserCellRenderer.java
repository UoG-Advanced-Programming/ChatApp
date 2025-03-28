package com.example.client.gui;

import javax.swing.*;
import java.awt.*;
import com.example.common.users.User;

/**
 * Custom cell renderer for displaying active users in a JList.
 * This renderer modifies the appearance of list items based on user attributes.
 */
public class ActiveUserCellRenderer extends DefaultListCellRenderer {

    /**
     * Overrides the default method to customize the rendering of user cells.
     *
     * @param list        The JList that displays the cells.
     * @param value       The value to be rendered (expected to be a User object).
     * @param index       The index of the cell in the list.
     * @param isSelected  Whether the cell is selected.
     * @param cellHasFocus Whether the cell has focus.
     * @return The customized component for rendering.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Ensure the provided value is a User instance before casting
        User user = (User) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Append "(Coordinator)" to the name if the user has coordinator privileges
        if (user.getIsCoordinator()) {
            label.setText(user.getUsername() + " (Coordinator)");
        } else {
            label.setText(user.getUsername());
        }

        // Set font style for better readability
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add padding around text for improved UI aesthetics
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Define custom colors for different user roles and selection states
        Color gold = new Color(255, 215, 0); // Standard gold color for coordinators
        Color darkGold = new Color(218, 165, 32); // Darker gold for selected coordinator
        Color selectedBlue = new Color(30, 144, 255); // Dodger blue for selected non-coordinator

        // Apply custom styling based on user role and selection state
        if (user.getIsCoordinator()) {
            if (isSelected) {
                label.setBackground(darkGold); // Highlight coordinator selection
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(gold); // Standard coordinator background
                label.setForeground(Color.BLACK);
            }
        } else if (isSelected) {
            label.setBackground(selectedBlue); // Highlight selected non-coordinator
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.LIGHT_GRAY); // Default background for unselected users
            label.setForeground(Color.BLACK);
        }

        return label;
    }
}
