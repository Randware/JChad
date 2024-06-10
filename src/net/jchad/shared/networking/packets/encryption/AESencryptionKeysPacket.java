package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class AESencryptionKeysPacket implements Packet {

        private final PacketType packet_type = PacketType.AES_ENCRYPTION_KEYS;

        private final String message_key;
        private final String message_initialization_vector;

        private final String communication_key;
        private final String communication_initialization_vector;

    public AESencryptionKeysPacket(String message_key, String message_initialization_vector, String communication_key, String communication_initialization_vector) {
        this.message_key = message_key;
        this.message_initialization_vector = message_initialization_vector;
        this.communication_key = communication_key;
        this.communication_initialization_vector = communication_initialization_vector;
    }

    public String getMessage_key() {
        return message_key;
    }

    public String getCommunication_key() {
        return communication_key;
    }

    public String getMessage_initialization_vector() {
        return message_initialization_vector;
    }

    public String getCommunication_initialization_vector() {
        return communication_initialization_vector;
    }
}
