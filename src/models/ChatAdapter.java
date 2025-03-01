package models;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ChatAdapter implements JsonDeserializer<Chat> {
    @Override
    public Chat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("groupName")) {
            return context.deserialize(json, GroupChat.class);  // Example subclass
        } else if (jsonObject.has("user1Id") && jsonObject.has("user2Id")) {
            return context.deserialize(json, PrivateChat.class);  // Example subclass
        }

        throw new JsonParseException("Unknown chat type: " + jsonObject);
    }
}
