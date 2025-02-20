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
    private Map<String, String> nameToIPMap = new HashMap<>();
    private Set<String> names;

    public CoordinatorManager(Set<PrintWriter> writers, Set<String> names) {
        this.writers = writers;
        this.names = names;
    }

    public synchronized void assignCoordinator(String name, PrintWriter writer, String ip) {
        writerToNameMap.put(writer, name);
        nameToIPMap.put(name, ip);

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
            nameToIPMap.remove(leavingName);

            if (!names.isEmpty()) {
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
        System.out.println("New coordinator assigned: " + coordinator);
    }

    public synchronized void removeUser(PrintWriter writer) {
        String userName = writerToNameMap.remove(writer);
        if (userName != null) {
            nameToIPMap.remove(userName);
        }
    }

    public synchronized void requestDetails(String requesterName, String targetName, PrintWriter requesterWriter) {
        if (!names.contains(targetName)) {
            requesterWriter.println("MESSAGE User " + targetName + " not found.");
            return;
        }

        // Notify the coordinator about the request
        for (PrintWriter writer : writers) {
            String coordinatorName = writerToNameMap.get(writer);
            if (coordinatorName != null && coordinatorName.equals(coordinator)) {
                writer.println("MESSAGE Coordinator: " + requesterName + " is requesting details for " + targetName + ".");
                writer.println("MESSAGE Type 'APPROVE " + requesterName + " " + targetName + "' to approve or 'DENY " + requesterName + " " + targetName + "' to deny.");
                return;
            }
        }
    }

    public synchronized void handleDetailsResponse(String requesterName, String targetName, boolean approved) {
        for (PrintWriter writer : writers) {
            String userName = writerToNameMap.get(writer);
            if (userName != null && userName.equals(requesterName)) {
                if (approved) {
                    String targetIP = nameToIPMap.get(targetName);
                    String coordinatorName = getCoordinator();
                    writer.println("MESSAGE Details for " + targetName + ": IP=" + targetIP + ", Name=" + targetName + ", Coordinator=" + coordinatorName);
                } else {
                    writer.println("MESSAGE Your request for " + targetName + "'s details was denied.");
                }
                return;
            }
        }
    }
}
