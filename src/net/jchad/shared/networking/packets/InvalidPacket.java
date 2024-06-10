package net.jchad.shared.networking.packets;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class InvalidPacket implements Packet{
    private final PacketType packet_type = PacketType.INVALID;

    private final ArrayList<PacketType> required_packet_type = new ArrayList<>();


    private final String message;

    public InvalidPacket(PacketType required_packet_type, String message) {
        this.required_packet_type.add(required_packet_type);
        this.message = message;
    }

    public InvalidPacket(List<PacketType> required_packet_types, String message) {
        this.required_packet_type.addAll(required_packet_types);
        this.message = message;
    }

        public PacketType getPacket_type() {
        return packet_type;
    }

    public List<PacketType> getRequired_packet_type() {
        return required_packet_type;
    }

    public String getMessage() {
        return message;
    }
}
