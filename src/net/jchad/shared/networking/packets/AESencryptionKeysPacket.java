package net.jchad.shared.networking.packets;

public class AESencryptionKeysPacket implements Packet {
    private final PacketType packet_type = PacketType.AES_ENCRYPTION_KEY;

    private final String messages_key;

    private final String communications_key;

    public AESencryptionKeysPacket(String messages_key, String communications_key) {
        this.messages_key = messages_key;
        this.communications_key = communications_key;
    }

    public String getMessages_key() {
        return messages_key;
    }

    public String getCommunications_key() {
        return communications_key;
    }
}
