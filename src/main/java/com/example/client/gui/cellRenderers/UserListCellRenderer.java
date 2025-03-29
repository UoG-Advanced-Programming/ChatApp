package com.example.client.gui.cellRenderers;

import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;

/**
 * The UserListCellRenderer class customizes the rendering of user cells in a JList.
 */
public class UserListCellRenderer extends DefaultListCellRenderer {
    /**
     * Customizes the rendering of each cell in the user list.
     *
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cell's index.
     * @param isSelected True if the specified cell is selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return The component used to render the cell.
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the superclass method to get the default label
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Check if the value is an instance of User
        if (value instanceof User user) {
            label.setText(user.getUsername()); // Display the username
        }

        // Customize appearance
        label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Set font to SansSerif, bold, size 14
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10)); // Add padding
        label.setOpaque(true); // Ensure the label is opaque
        label.setForeground(Color.WHITE); // Set text color to white

        // Set background color based on selection state
        if (isSelected) {
            label.setBackground(new Color(30, 144, 255)); // Dodger Blue for selection
        } else {
            label.setBackground(new Color(50, 50, 50)); // Dark gray background for non-selected cells
        }

        return label; // Return the customized label
    }
}