package server;

import java.util.Set;
import java.io.PrintWriter;

public class CoordinatorManager {
    private String coordinator = null;
    private Set<PrintWriter> writers;

    public CoordinatorManager(Set<PrintWriter> writers) {
        this.writers = writers;
    }

    public synchronized void assignCoordinator(String name) {
        if (coordinator == null) {
            coordinator = name;
            announceCoordinator();
        }
    }

    public synchronized String getCoordinator() {
        return coordinator;
    }

    public synchronized void reassignCoordinator(String leavingName) {
        if (coordinator != null && coordinator.equals(leavingName)) {
            coordinator = null;
            // Assign a new coordinator if there are other users
            if (!writers.isEmpty()) {
                for (PrintWriter writer : writers) {
                    coordinator = writer.toString(); // Assign the first available user as coordinator
                    break;
                }
                announceCoordinator();
            }
        }
    }

    private void announceCoordinator() {
        for (PrintWriter writer : writers) {
            writer.println("COORDINATOR " + coordinator);
        }
        System.out.println("Coordinator is now: " + coordinator);
    }
}