package net.jchad.shared.networking.packets;

import net.jchad.shared.networking.packets.defaults.*;
import net.jchad.shared.networking.packets.encryption.*;
import net.jchad.shared.networking.packets.messages.*;
import net.jchad.shared.networking.packets.password.PasswordFailedPacket;
import net.jchad.shared.networking.packets.password.PasswordRequestPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.password.PasswordSuccessPacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;
import net.jchad.shared.networking.packets.username.UsernameServerPacket;

public enum PacketType {
    BANNED(BannedPacket.class),
    NOT_WHITELISTED(NotWhitelistedPacket.class),
    CONNECTION_CLOSED(ConnectionClosedPacket.class),
    CONNECTION_ESTABLISHED(ConnectionEstablishedPacket.class),
    AES_ENCRYPTION_KEYS(AESencryptionKeysPacket.class),
    RSA_KEY_EXCHANGE(RSAkeyPacket.class),
    KEY_EXCHANGE_START(KeyExchangeStartPacket.class),
    RSA_KEY_ERROR(RSAkeyErrorPacket.class),
    KEY_EXCHANGE_END(KeyExchangeEndPacket.class),
    SERVER_INFORMATION_RESPONSE(ServerInformationResponsePacket.class), //The SERVER_INFORMATION packet provides information to the client
    SERVER_INFORMATION_REQUEST(ServerInformationRequestPacket.class),
    INVALID(InvalidPacket.class),
    CLIENT_MESSAGE(ClientMessagePacket.class), //This is the message packet that the client sends to the server
    SERVER_MESSAGE(ServerMessagePacket.class),
    STATUS_MESSAGE_SUCCESS(MessageStatusSuccessPacket.class),
    STATUS_MESSAGE_FAILED(MessageStatusFailedPacket.class),//This is the message packet that the server uses internally and sends to all clients. It has additional information.
    USERNAME_SERVER(UsernameServerPacket.class),
    USERNAME_CLIENT(UsernameClientPacket.class),
    PASSWORD_REQUEST(PasswordRequestPacket.class),
    PASSWORD_RESPONSE(PasswordResponsePacket.class),
    PASSWORD_SUCCESS(PasswordSuccessPacket.class),
    PASSWORD_FAILED(PasswordFailedPacket.class),
    JOIN_CHAT_REQUEST(JoinChatRequestPacket.class),
    JOIN_CHAT_RESPONSE(JoinChatResponsePacket.class);


    private final Class<? extends Packet> associatedPacket;

    PacketType(Class<? extends Packet> associatedPacket) {
        this.associatedPacket = associatedPacket;
    }


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

    public Class<? extends Packet> getAssociatedPacket() {
        return associatedPacket;
    }
}
