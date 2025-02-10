package client;

import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Scanner in;
    private ClientGUI gui;

    public ClientHandler(Scanner in, ClientGUI gui) {
        this.in = in;
        this.gui = gui;
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
            }
        }
    }
}

