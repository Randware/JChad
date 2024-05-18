package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class CommunicationInitializationVectorPacket implements Packet {
    private final PacketType packet_type = PacketType.AES_COM_INITIALIZATION_VECTOR;
    private final String initialization_vector;

    public CommunicationInitializationVectorPacket(String initialization_vector) {
        this.initialization_vector = initialization_vector;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }
}
