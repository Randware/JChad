package net.jchad.shared.packets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Every packet <b><u>has to</u></b> implement this interface.
 * This interface provides some useful methods for serialization and deserialization by using the {@link com.google.gson GSON} library.
 *
 */
public interface Packet{
    Gson gson = new Gson();

    /**
     * This methode serializes the current {@link net.jchad.shared.packets.Packet Packet} into json.
     * @return The json representation of the {@link net.jchad.shared.packets.Packet Packet} class (or its subclasses)
     */
    default String toJSON() {
        return gson.toJson(this);
    }

    /**
     * This deserializes JSON strings into the given objects.
     * @param jsonObjectString The JSON string that represents the Object that implements {@link net.jchad.shared.packets.Packet Packet}
     * @param classOfPacket The class of the Object that it should be deserialized to.
     * @return The deserialized subclass of {@link net.jchad.shared.packets.Packet Packet}
     * @throws JsonSyntaxException When the JSON string is not valid JSON this exception gets thrown.
     */
    static <T extends Packet> T fromJSON(String jsonObjectString, Class<T> classOfPacket) throws JsonSyntaxException {
        return gson.fromJson(jsonObjectString, classOfPacket);
    }
}
