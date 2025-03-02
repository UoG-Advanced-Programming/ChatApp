package com.example.models;

import com.google.gson.*;
import java.lang.reflect.Type;

public class CommunicationAdapter implements JsonDeserializer<Communication> {
    @Override
    public Communication deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("content") && jsonObject.has("sender")) {
            // If JSON contains "content" and "sender", it's a TextMessage
            return context.deserialize(json, TextMessage.class);
        } else if (jsonObject.has("user") && jsonObject.has("status")) {
            // If JSON contains "user" and "status", it's a UserUpdateMessage
            return context.deserialize(json, UserUpdateMessage.class);
        } else if (jsonObject.has("systemContent")) {
            // If JSON contains "systemContent", it's a SystemMessage
            return context.deserialize(json, SystemMessage.class);
        }

        throw new JsonParseException("Unknown message type: " + jsonObject);
    }
}
