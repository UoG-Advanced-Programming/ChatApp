package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.HashSet;

public class ClientGUI {
    private static ClientGUI instance;  // Singleton pattern for GUI instance

    private ChatClient client;
    private JFrame frame;
    private JTextField textField;
    private JTextArea messageArea;
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JButton sendButton, quitButton;

    private Set<String> connectedUsers = new HashSet<>();

    // Singleton pattern: Ensures only one instance of GUI
    public static ClientGUI getInstance(ChatClient client) {
        if (instance == null) {
            instance = new ClientGUI(client);
        }
        return instance;
    }

    private ClientGUI(ChatClient client) {
        this.client = client;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Chatter");
        textField = new JTextField(40);
        messageArea = new JTextArea(16, 40);
        messageArea.setEditable(false);
        textField.setEditable(false);

        sendButton = new JButton("Send");
        quitButton = new JButton("Quit");

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        // Layout improvements
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(new JLabel("Online Users:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        rightPanel.add(quitButton, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.disconnect();
            }
        });

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    public void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            if (!userList.isSelectionEmpty()) {
                message = "@private " + userList.getSelectedValue() + " " + message;
            }
            client.sendMessage(message);
            textField.setText("");
        }
    }

    public void showMessage(String message) {
        messageArea.append(message + "\n");
    }

    public void updateUsers(Set<String> users) {
        connectedUsers = users;
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    public void enableInput() {
        textField.setEditable(true);
    }

    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }
}
