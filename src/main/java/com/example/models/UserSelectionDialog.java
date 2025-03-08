package com.example.models;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class UserSelectionDialog extends JDialog {
    private final JList<User> userList;
    private boolean confirmed = false;

    public UserSelectionDialog(Frame parent, Set<User> users) {
        super(parent, "Select User", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Create a list model and populate it with users
        DefaultListModel<User> userListModel = new DefaultListModel<>();
        for (User user : users) {
            userListModel.addElement(user);
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
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

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
        setVisible(true);
        if (confirmed) {
            return userList.getSelectedValue();
        }
        return null;
    }
}
