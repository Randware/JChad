package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PublicRSAkeyErrorPacket implements Packet {
    private final PacketType packet_type = PacketType.RSA_KEY_ERROR;
    private final String error_message;

    public PublicRSAkeyErrorPacket(String error_message) {
        this.error_message = error_message;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }

    public String getError_message() {
        return error_message;
    }
}
