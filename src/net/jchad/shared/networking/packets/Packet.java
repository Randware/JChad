package net.jchad.shared.networking.packets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Every packet <b><u>has to</u></b> implement this interface.
 * This interface provides some useful methods for serialization and deserialization by using the {@link com.google.gson GSON} library.
 *
 */
public interface Packet{

    Gson gson = new Gson();


    /**
     * This methode serializes the current {@link Packet Packet} into json.
     * @return The json representation of the {@link Packet Packet} class (or its subclasses)
     */
    default String toJSON() {
        return gson.toJson(this);
    }

    /**
     * This deserializes JSON strings into the given objects.
     * @param jsonObjectString The JSON string that represents the Object that implements {@link Packet Packet}
     * @param classOfPacket The class of the Object that it should be deserialized to.
     * @return The deserialized subclass of {@link Packet Packet}
     * @throws JsonSyntaxException When the JSON string is not valid JSON this exception gets thrown.
     */
    static <T extends Packet> T fromJSON(String jsonObjectString, Class<T> classOfPacket) throws JsonSyntaxException {
        return gson.fromJson(jsonObjectString, classOfPacket);
    }

    /**
     * <p>Checks if the Packet is valid.</p>
     * A Packet may be invalid if a field is null or not parsable
     * @return if the packet is valid or not
     */
    default boolean isValid() {
        boolean notNull = true;
        boolean includesPacketType = false;
        Class<? extends Packet> clazz = this.getClass();
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                    if (field.get(this) == null && field.getAnnotation(IgnoreValidation.class) == null) {
                        notNull = false;
                        break;
                    }

                    if (field.getName().equals("packet_type") && field.get(this) != null) {
                        includesPacketType = true;
                    }
            } catch (Exception howWouldYouGetThatException) {
                break;
            }
        }
        return notNull && includesPacketType;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> currentClassFields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
        Class<?> parentClass = clazz.getSuperclass();

        if(parentClass != null) {
             currentClassFields.addAll(getAllFields(parentClass));
        }
        return currentClassFields;
    }
}
