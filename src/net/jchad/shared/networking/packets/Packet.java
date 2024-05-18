package net.jchad.shared.networking.packets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface provides some useful methods for Packets. Like serialization and deserialization by using the {@link com.google.gson GSON} library.
 *
 * <p>Every packet <b><u>has to</u></b> implement this interface.</p>
 * <p><u><b><font color="red">If any packet breaks these conventions the {@link Packet#isValid()} will return false!</font></b></u></p>
 * <ul>
 *      <li><p>Every subclass <b>has to declare a {@link PacketType packet_type}</b> field with the type of {@link PacketType} </p></li>
 *      <li><p>No variables are allowed to be null, expect those who are annotated with the {@link IgnoreValidation @IgnoreValidation} annotation.</p></li>
 * </ul>
 *  <p>Here are examples of two packets that are not valid</p>
 *  Invalid example one:
 *     <blockquote><pre>
 *     public class InvalidPacket implements Packet{
 *
 *     private final PacketType packet_type = PacketType.TEST;
 *     private final String testString = null; //THE VARIABLE IS NOT ALLOWED TO BE NULL
 *
 *     {@code @IgnoreValidation}
 *     private final String testString2 = null;
 *     //This variable is valid because of the annotation
 *     }
 *   </pre></blockquote>
 *   Invalid example two:
 *   <blockquote><pre>
 *     public class InvalidPacket2 implements Packet{
 *
 *     private final PacketType packetType = PacketType.TEST;
 *     //This is invalid, the variable has to have the exact name "packet_type"
 *     private final String testString = "Hello Network!";
 *     }
 *     </pre></blockquote>
 *
 *     <p><u><b>If you dislike the {@link Packet#isValid()} methode or you want to extend its functionality you can just {@code @Override} it and provide your own implementation.</b></u></p>
 *     <p>It is generally not recommended to override the {@link Packet#isValid()} methode completely, but there are certain use cases where it is really useful to override the methode</p>
 *      <p>Here are examples of an overwritten {@link Packet#isValid()} methode: </p>
 *      <blockquote><pre>
 *       public class ValidPacket implements Packet{
 *
 *       private final PacketType packet_type = PacketType.TEST;
 *       private final String base64Message = "SGFkaSB0aGUgR29hdA==";
 *
 *       {@code @Override}
 *       public boolean isValid() {
 *         boolean isValidBase64 = true;
 *         try {
 *             Base64.getDecoder().decode(base64Message);
 *         } catch (IllegalArgumentException e) {
 *             isValidBase64 = false;
 *         }
 *         return Packet.super.isValid() && isValidBase64;
 *       }}
 *      </pre></blockquote>
 *      <p>The example uses the default methode to check if all fields are not null. Additionally it checks if the {@code String base64Message} is valid Base64</p>
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

                    if (field.getName().equals("packet_type")) {
                        if (field.get(this) != null && field.get(this) instanceof PacketType) {
                            includesPacketType = true;
                        } else {
                            break;
                        }
                    }
            } catch (Exception howWouldYouGetThatException) {
                break;
            }
        }
        return notNull && includesPacketType;
    }

    /**
     * This retrieves all private/public/protected/... fields of the given class and of all its super classes with the help of recursion
     * @param clazz The class which fields get returned
     * @return All fields of the class
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> currentClassFields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
        Class<?> parentClass = clazz.getSuperclass();

        if(parentClass != null) {
             currentClassFields.addAll(getAllFields(parentClass));
        }
        return currentClassFields;
    }


}
