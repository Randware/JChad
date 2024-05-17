package net.jchad.shared.networking.packets.username;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class UsernameResponsePacket implements Packet {
    private final PacketType packet_type = PacketType.USERNAME_RESPONSE;
    private final UsernameResponseType username_response_type;
    private final String username_response_reason;

    public UsernameResponsePacket(UsernameResponseType username_response_type, String username_response_reason) {
        this.username_response_type = username_response_type;
        this.username_response_reason = username_response_reason;
    }

    public enum UsernameResponseType {
        ERROR_USERNAME_TAKEN,
        ERROR_USERNAME_INAPROPRIATE,
        ERROR_USERNAME_INVALID,
        SUCCESS_USERNAME_SET;
    }
}
