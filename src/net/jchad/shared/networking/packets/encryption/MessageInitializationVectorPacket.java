package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class MessageInitializationVectorPacket implements Packet {
    private final PacketType packet_type = PacketType.AES_MSG_INITIALIZATION_VECTOR;
    private final String initialization_vector;

    public MessageInitializationVectorPacket(String initialization_vector) {
        this.initialization_vector = initialization_vector;
    }

    public String getInitialization_vector() {
        return initialization_vector;
    }
}
