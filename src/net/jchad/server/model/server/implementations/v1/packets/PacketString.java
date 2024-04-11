package net.jchad.server.model.server.implementations.v1.packets;

public interface PacketString<T extends Enum<T>> {

    /**
     * Formats the enum string to a better readable name
     * Replaces underscores with spaces and makes every letter (except for the first one) to lower cas
     * @return "ERROR_AUTHENTICATOR" -> "Error authentication"
     */
    default String valueToString() {
        String thisEnum = toString();
        return  thisEnum.charAt(0) +  thisEnum.substring(1).toLowerCase().replace("_", " ");
    }

    /**
     * Makes a string to an enum again
     * @param stringEnum The string representation of the enum
     * @return the "reconstructed" enum
     */
    public T stringToValue(String stringEnum);

    /**
     * Makes the opposite thing of valueToString()
     * Replaces spaces with underscores and every letter to uppercase
     * @return "Error authentication" -> "ERROR_AUTHENTICATOR"
     */
    default String enumName() {
        return toString().toUpperCase().replace(" ", "_");
    }

}
