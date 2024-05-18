package net.jchad.shared.networking.packets;

public enum PacketType {
    BANNED ,
    NOT_WHITELISTED,
    CONNECTION_CLOSED,
    AES_MSG_ENCRYPTION_KEY,
    AES_MSG_INITIALIZATION_VECTOR,
    AES_COM_ENCRYPTION_KEY, //Why not AES_COMMUNICATIONS_ENCRYPTION_KEY? I had to shorten the name in order to encrypt it. The name was to long for rsa
    AES_COM_INITIALIZATION_VECTOR,
    RSA_KEY_EXCHANGE,
    KEY_EXCHANGE_START,
    RSA_KEY_ERROR,
    KEY_EXCHANGE_END,
    SERVER_INFORMATION, //The SERVER_INFORMATION packet provides information to the client
    INVALID,
    CLIENT_MESSAGE, //This is the message packet that the client sends to the server
    SERVER_MESSAGE,
    STATUS_MESSAGE, //This is the message packet that the server uses internally and sends to all clients. It has additional information.
    USERNAME,
    PASSWORD,
    LOAD_CHAT,
    CONNECTION_ESTABLISHED;



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
