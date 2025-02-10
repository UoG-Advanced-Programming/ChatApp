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
            if (line.startsWith("NAMEACCEPTED")) {
                gui.setTitle("Chatter - " + line.substring(13));
                gui.enableInput();
            } else if (line.startsWith("MESSAGE")) {
                gui.showMessage(line.substring(8));
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
