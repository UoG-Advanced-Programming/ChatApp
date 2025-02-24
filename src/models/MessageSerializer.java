package models;

import com.google.gson.Gson;

public class MessageSerializer {
    private static final Gson gson = new Gson();

    // Method to serialize Message object to JSON
    public static String serialize(Message message) {
        return gson.toJson(message);
    }

    // Method to deserialize JSON string to Message object
    public static Message deserialize(String json) {
        return gson.fromJson(json, Message.class);
    }
}
