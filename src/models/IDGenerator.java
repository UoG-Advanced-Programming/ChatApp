package models;

import java.util.UUID;

public class IDGenerator {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
