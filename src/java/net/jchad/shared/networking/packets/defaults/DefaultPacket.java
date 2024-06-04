package net.jchad.shared.networking.packets.defaults;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class DefaultPacket implements Packet {
    private final PacketType packet_type;
    private final String message;

    public DefaultPacket(PacketType packet_type, String message) {
        this.packet_type = packet_type;
        this.message = message;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DefaultPacket: " +
                "packet_type=" + packet_type +
                ", message=" + message + ";";
    }
}
