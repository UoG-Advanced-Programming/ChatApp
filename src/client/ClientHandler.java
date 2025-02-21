package client;

import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ClientHandler implements Runnable {
    private Scanner in;
    private ClientGUI gui;
    private ChatClient client;

    public ClientHandler(Scanner in, ClientGUI gui, ChatClient client) {
        this.in = in;
        this.gui = gui;
        this.client = client;
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String line = in.nextLine();

            System.out.println(line);

            if (line.startsWith("NAMEACCEPTED")) {
                gui.setTitle("Chatter - " + line.substring(13));
                gui.enableInput();
            } else if (line.startsWith("MESSAGE")) {
                String sender = line.split(" ", 3)[1].split(":")[0];
                if (!sender.equals(client.getUsername())) {
                    gui.showMessage("General", line.substring(8));  // Show in General chat
                }
            } else if (line.startsWith("PRIVATE")) {
                String[] parts = line.split(" ", 3);
                if (parts.length == 3) {
                    String sender = parts[1];   // Sender's name
                    String msgContent = parts[2];  // The actual message

                    if (!sender.equals(client.getUsername())) {
                        gui.showMessage(sender, msgContent);
                    }
                }
            } else if (line.startsWith("USERS")) {
                Set<String> users = new HashSet<>();
                String[] userArray = line.substring(6).split(",");
                for (String user : userArray) {
                    if (!user.trim().isEmpty()) {
                        users.add(user.trim());
                    }
                }
                client.updateUserList(users);
            }
        }
    }
}
