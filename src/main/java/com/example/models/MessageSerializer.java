package com.example.models;

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageSerializer {
    private static final Gson gson;

    static {
        // Register subclasses for Communication
        RuntimeTypeAdapterFactory<Communication> communicationAdapter =
                RuntimeTypeAdapterFactory.of(Communication.class, "type")
                        .registerSubtype(TextMessage.class, "TEXT")
                        .registerSubtype(UserUpdateMessage.class, "USER_UPDATE")
                        .registerSubtype(SystemMessage.class, "SYSTEM");

        // Register subclasses for Chat
        RuntimeTypeAdapterFactory<Chat> chatAdapter =
                RuntimeTypeAdapterFactory.of(Chat.class, "type")
                        .registerSubtype(GroupChat.class, "group")
                        .registerSubtype(PrivateChat.class, "private");

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(communicationAdapter)
                .registerTypeAdapterFactory(chatAdapter)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static String serialize(Communication message) {
        return gson.toJson(message);
    }

    public static Communication deserialize(String json) {
        return gson.fromJson(json, Communication.class);
    }

    // Custom TypeAdapter for LocalDateTime
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}
