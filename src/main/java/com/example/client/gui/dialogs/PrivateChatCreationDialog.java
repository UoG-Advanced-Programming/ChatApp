package com.example.client.gui.dialogs;

import com.example.client.gui.cellRenderers.UserListCellRenderer;
import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * The PrivateChatCreationDialog class is a dialog for selecting a user to start a private chat with.
 */
public class PrivateChatCreationDialog extends JDialog {
    private final JList<User> userList; // The list of users to select from
    private boolean confirmed = false; // Flag to indicate if the dialog was confirmed

    /**
     * Constructs a PrivateChatCreationDialog instance.
     *
     * @param parent      The parent frame of the dialog
     * @param users       The set of users to select from
     * @param currentUser The current user (to be excluded from the selection list)
     */
    public PrivateChatCreationDialog(Frame parent, Set<User> users, User currentUser) {
        super(parent, "Select User", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Create a list model and populate it with users (excluding the current user)
        DefaultListModel<User> userListModel = new DefaultListModel<>();
        for (User user : users) {
            if (!user.equals(currentUser)) { // Exclude the current user
                userListModel.addElement(user);
            }
        }

        // Create a JList with the list model
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
        userList.setCellRenderer(new UserListCellRenderer()); // Custom renderer for users

        // Add the list to a scroll pane
        JScrollPane scrollPane = new JScrollPane(userList);

        // Create buttons for confirmation and cancellation
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            confirmed = true;
            dispose(); // Close the dialog
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose()); // Close the dialog without confirmation

        // Add components to the dialog
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows the dialog and returns the selected user.
     *
     * @return The selected user, or null if the dialog was canceled.
     */
    public User getSelectedUser() {
        setVisible(true); // Show the dialog
        if (confirmed) {
            return userList.getSelectedValue(); // Return the selected user if confirmed
        }
        return null; // Return null if the dialog was canceled
    }
}