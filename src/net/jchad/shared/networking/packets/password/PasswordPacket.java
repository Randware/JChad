package net.jchad.shared.networking.packets.password;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public abstract class PasswordPacket implements Packet {

    private final PacketType packet_type = PacketType.PASSWORD;
    private final PasswordPacketType password_packet_type;

    public PasswordPacket(PasswordPacketType password_packet_type) {
        this.password_packet_type = password_packet_type;
    }

    public enum PasswordPacketType {
        PASSWORD_REQUEST,
        PASSWORD_RESPONSE,
        PASSWORD_SUCCESS,
        PASSWORD_FAILED;
    }

    public PasswordPacketType getPassword_packet_type() {
        return password_packet_type;
    }
}
