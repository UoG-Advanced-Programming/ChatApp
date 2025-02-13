package server;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;
import java.util.Random;

public class CoordinatorManager {
    private String coordinator = null;
    private Set<PrintWriter> writers;
    private Map<PrintWriter, String> writerToNameMap = new HashMap<>();
    private Set<String> names;

    public CoordinatorManager(Set<PrintWriter> writers, Set<String> names) {
        this.writers = writers;
        this.names = names;
    }

    public synchronized void assignCoordinator(String name, PrintWriter writer) {
        if (coordinator == null) {
            coordinator = name;
            writerToNameMap.put(writer, name);
            announceCoordinator();
        }
    }

    public synchronized String getCoordinator() {
        return coordinator;
    }

    public synchronized void reassignCoordinator(String leavingName) {
        if (coordinator != null && coordinator.equals(leavingName)) {
            coordinator = null;
            writerToNameMap.values().remove(leavingName); // Remove the old coordinator
            
            if (!names.isEmpty()) {
                // Randomly select a new coordinator
                String[] nameArray = names.toArray(new String[0]);
                coordinator = nameArray[new Random().nextInt(nameArray.length)];
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

    public synchronized void removeUser(PrintWriter writer) {
        writerToNameMap.remove(writer);
    }
}
