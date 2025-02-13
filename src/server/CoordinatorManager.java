package server;

public class CoordinatorManager {
    private String coordinator = null;

    public synchronized void assignCoordinator(String name) {
        if (coordinator == null) {
            coordinator = name;
            System.out.println("Coordinator assigned: " + coordinator);
        }
    }

    public synchronized String getCoordinator() {
        return coordinator;
    }

    public synchronized void reassignCoordinator(String leavingName) {
        if (coordinator != null && coordinator.equals(leavingName)) {
            coordinator = null;
            System.out.println("Coordinator left, new coordinator will be assigned.");
        }
    }
}