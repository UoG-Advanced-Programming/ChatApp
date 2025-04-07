package com.example.client.gui.cellRenderers;

import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;

/**
 * Custom renderer for displaying chat entries in a JList.
 * This renderer customizes the appearance of each chat item, including icons, colors, and styles.
 */
public class ChatListCellRenderer extends DefaultListCellRenderer {
    private final User currentUser; // Current User
    private static final int MAX_NAME_LENGTH = 15; // Maximum characters before truncating chat names
    private static final Color PRIVATE_CHAT_COLOR = new Color(100, 149, 237); // Cornflower Blue for private chats
    private static final Color GROUP_CHAT_COLOR = new Color(255, 140, 0); // Dark Orange for group chats
    private static final Color SELECTED_COLOR = new Color(144, 238, 144); // Light Green for selected chats
    private static final Color INACTIVE_COLOR = Color.GRAY; // Gray for inactive private chats

    /**
     * Constructs a chat list cell renderer with context of the current user.
     *
     * @param currentUser The user who is viewing the chat list.
     */
    public ChatListCellRenderer(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Overrides the default rendering method to customize how chat items appear.
     *
     * @param list       The JList that displays the chat items.
     * @param value      The chat object (either PrivateChat or GroupChat).
     * @param index      The index of the cell in the list.
     * @param isSelected Whether the cell is selected.
     * @param cellHasFocus Whether the cell has focus.
     * @return The customized component for rendering the chat entry.
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Ensure the label background blends smoothly with the panel
        setOpaque(false);
        setFont(new Font("SansSerif", Font.BOLD, 14)); // Modern, bold font for readability
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for a clean layout

        String chatName = "";

        // Determine chat type and set styling accordingly
        if (value instanceof PrivateChat privateChat) {
            chatName = privateChat.getDisplayName(currentUser);
            setForeground(privateChat.isActive() ? PRIVATE_CHAT_COLOR : INACTIVE_COLOR);
        } else if (value instanceof GroupChat groupChat) {
            chatName = groupChat.getDisplayName(currentUser);
            setForeground(GROUP_CHAT_COLOR);
        }

        // Truncate long chat names to maintain UI consistency
        String displayedName = truncate(chatName);
        setText(displayedName);
        setToolTipText(chatName); // Display full chat name on hover

        // Apply background color based on selection state
        setBackground(isSelected ? SELECTED_COLOR : Color.WHITE);

        // Wrap the label inside a custom rounded panel for a sleek UI
        return new RoundedPanel(this, isSelected);
    }

    /**
     * Helper method to truncate long chat names for better UI alignment.
     *
     * @param name The original chat name.
     * @return The truncated chat name with ellipsis if necessary.
     */
    private String truncate(String name) {
        return (name.length() > ChatListCellRenderer.MAX_NAME_LENGTH) ? name.substring(0, ChatListCellRenderer.MAX_NAME_LENGTH - 3) + "..." : name;
    }

    /**
     * Custom panel that renders chat items with rounded backgrounds and gradient effects.
     */
    private static class RoundedPanel extends JPanel {
        private final boolean isSelected;

        /**
         * Constructs a rounded panel wrapper for the chat cell.
         *
         * @param content    The JLabel component containing chat text and icon.
         * @param isSelected Whether the chat item is currently selected.
         */
        public RoundedPanel(JComponent content, boolean isSelected) {
            this.isSelected = isSelected;
            setLayout(new BorderLayout());
            add(content, BorderLayout.CENTER);
            setOpaque(false); // Transparent to allow for custom rendering
        }

        /**
         * Custom paint method to render rounded backgrounds with a gradient effect.
         *
         * @param g The Graphics object used for painting.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = 20; // Rounded corner radius

            // Define gradient background colors based on selection state
            Color startColor = isSelected ? SELECTED_COLOR.brighter() : Color.WHITE;
            Color endColor = isSelected ? SELECTED_COLOR.darker() : new Color(230, 230, 230); // Subtle gray gradient

            // Apply gradient fill to the rounded rectangle
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2.setPaint(gp);
            g2.fillRoundRect(5, 5, width - 10, height - 10, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
