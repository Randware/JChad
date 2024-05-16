package net.jchad.shared.networking.packets;

public class PasswordPacket implements Packet{

    private final PacketType packet_type = PacketType.PASSWORD;
    private final PasswordPacketType password_packet_type;

    public PasswordPacket(PasswordPacketType password_packet_type) {
        this.password_packet_type = password_packet_type;
    }

    public enum PasswordPacketType {
        PASSWORD_REQUEST,
        PASSWORD_RESPONSE,
        PASSWORD_SUCCESS
    }

    public PacketType getPacket_type() {
        return packet_type;
    }
}
