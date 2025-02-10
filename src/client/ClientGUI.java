package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {
    private ChatClient client;
    private JFrame frame;
    private JTextField textField;
    private JTextArea messageArea;

    public ClientGUI(ChatClient client) {
        this.client = client;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Chatter");
        textField = new JTextField(50);
        messageArea = new JTextArea(16, 50);
        messageArea.setEditable(false);
        textField.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(textField.getText());
                textField.setText("");
            }
        });
    }

    public String promptForName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen Name Selection", JOptionPane.PLAIN_MESSAGE);
    }

    public void showMessage(String message) {
        messageArea.append(message + "\n");
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

