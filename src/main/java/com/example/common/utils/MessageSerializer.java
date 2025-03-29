package com.example.common.utils;

import com.example.common.messages.*;
import com.example.common.chats.Chat;
import com.example.common.chats.ChatType;
import com.example.common.chats.GroupChat;
import com.example.common.chats.PrivateChat;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for serializing and deserializing messages.
 */
public class MessageSerializer {
    private static final Gson gson; // Gson instance for serialization and deserialization

    static {
        // Register subclasses for Communication
        RuntimeTypeAdapterFactory<Communication> communicationAdapter =
                RuntimeTypeAdapterFactory.of(Communication.class, "type")
                        .registerSubtype(TextMessage.class, CommunicationType.TEXT.name())
                        .registerSubtype(UserUpdateMessage.class, CommunicationType.USER_UPDATE.name())
                        .registerSubtype(SystemMessage.class, CommunicationType.SYSTEM.name());

        // Register subclasses for Chat
        RuntimeTypeAdapterFactory<Chat> chatAdapter =
                RuntimeTypeAdapterFactory.of(Chat.class, "type")
                        .registerSubtype(GroupChat.class, ChatType.GROUP.name())
                        .registerSubtype(PrivateChat.class, ChatType.PRIVATE.name());

        // Initialize the Gson instance with custom adapters
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(communicationAdapter)
                .registerTypeAdapterFactory(chatAdapter)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Serializes a Communication object to its JSON representation.
     *
     * @param message The Communication object to serialize
     * @return The JSON representation of the message
     */
    public static String serialize(Communication message) {
        return gson.toJson(message);
    }

    /**
     * Deserializes a JSON string to a Communication object.
     *
     * @param json The JSON string to deserialize
     * @return The deserialized Communication object
     */
    public static Communication deserialize(String json) {
        Communication message = gson.fromJson(json, Communication.class);
        if (message.getType() == null) {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has("type")) {
                message.setType(CommunicationType.valueOf(jsonObject.get("type").getAsString()));
            }
        }
        return message;
    }

    /**
     * Custom TypeAdapter for LocalDateTime to handle serialization and deserialization.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Formatter for LocalDateTime

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(formatter)); // Serialize LocalDateTime to JSON string
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), formatter); // Deserialize JSON string to LocalDateTime
        }
    }
}