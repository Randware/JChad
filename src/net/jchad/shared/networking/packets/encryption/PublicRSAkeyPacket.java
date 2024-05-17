package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PublicRSAkeyPacket implements Packet {
    private final PacketType packet_type = PacketType.RSA_PUBLIC_KEY;
    private final String publicKey;

    public PublicRSAkeyPacket(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
