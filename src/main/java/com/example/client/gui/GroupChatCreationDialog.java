package com.example.client.gui;

import com.example.common.users.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Dialog for creating a new group chat.
 * Allows the user to enter a group name and select participants.
 */
public class GroupChatCreationDialog extends JDialog {
    private final JTextField chatNameField; // Input field for group chat name
    private final JList<User> userList; // List of users available for selection
    private boolean confirmed = false; // Flag to check if the user confirmed creation

    /**
     * Constructs a modal dialog for creating a group chat.
     *
     * @param parent      The parent frame for positioning the dialog.
     * @param users       The set of users available for selection.
     * @param currentUser The user creating the chat (excluded from selection).
     */
    public GroupChatCreationDialog(Frame parent, Set<User> users, User currentUser) {
        super(parent, "Create Group Chat", true); // Modal dialog
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Panel for entering the group chat name
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
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allows multi-selection
        userList.setCellRenderer(new UserListCellRenderer()); // Custom renderer for user display

        JScrollPane scrollPane = new JScrollPane(userList); // Scrollable user list

        // Create "Create" and "Cancel" buttons
        JButton okButton = new JButton("Create");
        okButton.addActionListener(e -> {
            if (validateInputs()) { // Ensure input validation before confirming
                confirmed = true;
                dispose(); // Close dialog
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose()); // Close dialog on cancel

        // Panel to hold buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Assemble components in the dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(namePanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Validates user input before allowing group chat creation.
     *
     * @return true if inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        String chatName = chatNameField.getText().trim();

        // Ensure the group chat has a valid name
        if (chatName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide a name for the group chat.",
                    "Invalid Chat Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Ensure at least one user is selected for the group chat
        if (userList.getSelectedValuesList().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one user for the group chat.",
                    "No Users Selected", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Displays the dialog and waits for user input.
     */
    public void showDialog() {
        setVisible(true);
    }

    /**
     * Checks if the user confirmed the group chat creation.
     *
     * @return true if the user confirmed, false otherwise.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Retrieves the entered group chat name.
     *
     * @return The group chat name as a trimmed string.
     */
    public String getChatName() {
        return chatNameField.getText().trim();
    }

    /**
     * Retrieves the selected users for the group chat.
     *
     * @return A list of selected users.
     */
    public List<User> getSelectedUsers() {
        return userList.getSelectedValuesList();
    }
}
