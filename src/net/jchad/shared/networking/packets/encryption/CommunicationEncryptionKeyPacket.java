package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class CommunicationEncryptionKeyPacket implements Packet {
    private final PacketType packet_type = PacketType.AES_COM_ENCRYPTION_KEY;

    private final String communication_key;


    public CommunicationEncryptionKeyPacket(String communication_key) {
        this.communication_key = communication_key;
    }

    public String getCommunication_key() {
        return communication_key;
    }
}
