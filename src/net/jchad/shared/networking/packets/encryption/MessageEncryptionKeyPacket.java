package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class MessageEncryptionKeyPacket implements Packet {
    private final PacketType packet_type = PacketType.AES_MSG_ENCRYPTION_KEY;

    private final String messages_key;


    public MessageEncryptionKeyPacket(String messages_key) {
        this.messages_key = messages_key;
    }

    public String getMessages_key() {
        return messages_key;
    }

}
