package com.example.common.utils;

import java.util.UUID;

/**
 * Utility class for generating unique identifiers.
 */
public class IDGenerator {

    /**
     * Generates a new UUID (Universally Unique Identifier) as a string.
     *
     * @return A randomly generated UUID string.
     */
    public static String generateUUID() {
        // Generate a random UUID and convert it to a string
        return UUID.randomUUID().toString();
    }
}