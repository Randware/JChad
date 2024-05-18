package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PublicRSAkeyPacket implements Packet {
    private final PacketType packet_type = PacketType.RSA_KEY_EXCHANGE;
    private final String public_key;

    public PublicRSAkeyPacket(String public_key) {
        this.public_key = public_key;
    }

    public String getPublic_key() {
        return public_key;
    }
}
