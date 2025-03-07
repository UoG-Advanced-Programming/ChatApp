package com.example.models;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class GroupChatCreationDialog extends JDialog {
    private JTextField chatNameField;
    private JList<User> userList;
    private boolean confirmed = false;

    public GroupChatCreationDialog(Frame parent, Set<User> users) {
        super(parent, "Create Group Chat", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Create a panel for the chat name input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Group Chat Name:"), BorderLayout.WEST);
        chatNameField = new JTextField();
        namePanel.add(chatNameField, BorderLayout.CENTER);

        // Create a list model and populate it with users
        DefaultListModel<User> userListModel = new DefaultListModel<>();
        for (User user : users) {
            userListModel.addElement(user);
        }

        // Create a JList with the list model
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allow multiple selection
        userList.setCellRenderer(new UserListCellRenderer()); // Custom renderer for users

        // Add the list to a scroll pane
        JScrollPane scrollPane = new JScrollPane(userList);

        // Create buttons for confirmation and cancellation
        JButton okButton = new JButton("Create");
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
        getContentPane().add(namePanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows the dialog and returns the group chat details.
     *
     * @return An array containing the chat name and selected users, or null if the dialog was canceled.
     */
    public Object[] getGroupChatDetails() {
        setVisible(true);
        if (confirmed) {
            String chatName = chatNameField.getText().trim();
            List<User> selectedUsers = userList.getSelectedValuesList();
            if (!chatName.isEmpty() && !selectedUsers.isEmpty()) {
                return new Object[]{chatName, selectedUsers};
            }
        }
        return null;
    }
}
