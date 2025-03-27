package com.example.client.gui;

import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class GroupChatCreationDialog extends JDialog {
    private final JTextField chatNameField;
    private final JList<User> userList;
    private boolean confirmed = false;

    public GroupChatCreationDialog(Frame parent, Set<User> users, User currentUser) {
        super(parent, "Create Group Chat", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Create a panel for the chat name input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Group Chat Name:"), BorderLayout.WEST);
        chatNameField = new JTextField();
        namePanel.add(chatNameField, BorderLayout.CENTER);

        // Populate the user list, excluding the current user
        DefaultListModel<User> userListModel = new DefaultListModel<>();
        for (User user : users) {
            if (!user.equals(currentUser)) {
                userListModel.addElement(user);
            }
        }

        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setCellRenderer(new UserListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(userList);

        // Create buttons
        JButton okButton = new JButton("Create");
        okButton.addActionListener(e -> {
            if (validateInputs()) {
                confirmed = true;
                dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(namePanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Validates user input.
     */
    private boolean validateInputs() {
        String chatName = chatNameField.getText().trim();
        if (chatName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide a name for the group chat.",
                    "Invalid Chat Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (userList.getSelectedValuesList().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one user for the group chat.",
                    "No Users Selected", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Shows the dialog and waits for user input.
     */
    public void showDialog() {
        setVisible(true);
    }

    /**
     * Returns whether the user confirmed the dialog.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the entered group chat name.
     */
    public String getChatName() {
        return chatNameField.getText().trim();
    }

    /**
     * Returns the selected users.
     */
    public List<User> getSelectedUsers() {
        return userList.getSelectedValuesList();
    }
}
