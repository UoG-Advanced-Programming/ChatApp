package com.example.client.gui;

import com.example.common.chats.Chat;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;

import javax.swing.*;
import java.awt.*;

public class ChatListCellRenderer extends DefaultListCellRenderer {
    private static final int MAX_NAME_LENGTH = 15; // Max characters before truncating
    private static final Color PRIVATE_CHAT_COLOR = new Color(100, 149, 237); // Cornflower Blue
    private static final Color GROUP_CHAT_COLOR = new Color(255, 140, 0); // Dark Orange
    private static final Color SELECTED_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color INACTIVE_COLOR = Color.GRAY;

    private static final ImageIcon PRIVATE_ICON = new ImageIcon("icons/private_chat.png"); // Replace with actual path
    private static final ImageIcon GROUP_ICON = new ImageIcon("icons/group_chat.png"); // Replace with actual path

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setOpaque(false);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // More spacing for a modern look

        String chatName = "";
        if (value instanceof PrivateChat privateChat) {
            chatName = privateChat.getName();
            setForeground(privateChat.isActive() ? PRIVATE_CHAT_COLOR : INACTIVE_COLOR);
            setIcon(PRIVATE_ICON);
        } else if (value instanceof GroupChat groupChat) {
            chatName = groupChat.getName();
            setForeground(GROUP_CHAT_COLOR);
            setIcon(GROUP_ICON);
        }

        // Handle long names
        String displayedName = truncate(chatName, MAX_NAME_LENGTH);
        setText(displayedName);
        setToolTipText(chatName); // Show full name on hover

        // Add a subtle gradient background for a premium look
        setBackground(isSelected ? SELECTED_COLOR : Color.WHITE);

        return new RoundedPanel(this, isSelected);
    }

    // Helper method to truncate long names
    private String truncate(String name, int maxLength) {
        return (name.length() > maxLength) ? name.substring(0, maxLength - 3) + "..." : name;
    }

    // Custom panel to render rounded backgrounds
    private static class RoundedPanel extends JPanel {
        private final JComponent content;
        private final boolean isSelected;

        public RoundedPanel(JComponent content, boolean isSelected) {
            this.content = content;
            this.isSelected = isSelected;
            setLayout(new BorderLayout());
            add(content, BorderLayout.CENTER);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = 20; // Rounded corners

            // Background gradient
            Color startColor = isSelected ? SELECTED_COLOR.brighter() : Color.WHITE;
            Color endColor = isSelected ? SELECTED_COLOR.darker() : new Color(230, 230, 230);

            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2.setPaint(gp);
            g2.fillRoundRect(5, 5, width - 10, height - 10, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
