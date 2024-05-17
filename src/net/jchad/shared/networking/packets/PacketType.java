package net.jchad.shared.networking.packets;

public enum PacketType {
    BANNED ,
    NOT_WHITELISTED,
    CONNECTION_CLOSED,
    AES_ENCRYPTION_KEY,
    RSA_PUBLIC_KEY,
    SERVER_INFORMATION, //The SERVER_INFORMATION packet provides information to the client
    INVALID,
    CLIENT_MESSAGE, //This is the message packet that the client sends to the server
    SERVER_MESSAGE, //This is the message packet that the server uses internally and sends to all clients. It has additional information.
    USERNAME,
    PASSWORD,
    JOIN_CHAT;



    /**
     * Gets a more readable form of the enum:
     * <ul>
     * <li>NOT_WHITELISTED => Not whitelisted</li>
     * <li>BANNED          => Banned</li>
     * </ul>
     * @return A more readable representation of the enum
     */
    public String toReadableString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", "");
    }

    /**
     *
     * @param readableStringOfEnum The readable name of the enum that was generated with {@link PacketType#toReadableString()}
     * @return The enum of the given readableString
     * @throws IllegalArgumentException  if this enum type has no constant with the specified name
     */
    public static PacketType valueOfReadableString(String readableStringOfEnum) throws IllegalArgumentException {
        return valueOf(readableStringOfEnum.replace(" ", "_").toUpperCase());
    }


}
