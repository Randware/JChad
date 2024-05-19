package net.jchad.shared.networking.packets;

public class InvalidPacket implements Packet{
    private final PacketType packet_type = PacketType.INVALID;

    @IgnoreValidation
    private final PacketType required_packet_type;


    private final String message;

    public InvalidPacket(PacketType required_packet_type, String message) {
        this.required_packet_type = required_packet_type;
        this.message = message;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }

    public PacketType getRequired_packet_type() {
        return required_packet_type;
    }

    public String getMessage() {
        return message;
    }
}
