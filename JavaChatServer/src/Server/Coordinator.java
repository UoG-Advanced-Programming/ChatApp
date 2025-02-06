package Server;

public class Coordinator {
    private static ClientHandler currentCoordinator;

    public static void assignCoordinator(ClientHandler newCoordinator) {
        if (currentCoordinator != null) {
            currentCoordinator.setCoordinator(false);
        }
        currentCoordinator = newCoordinator;
        newCoordinator.setCoordinator(true);
        System.out.println("New Coordinator: " + newCoordinator.getClientID());
    }

    public static ClientHandler getCoordinator() {
        return currentCoordinator;
    }
}
