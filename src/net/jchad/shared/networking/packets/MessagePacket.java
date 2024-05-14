package net.jchad.shared.networking.packets;

public class MessagePacket implements Packet {
    private final PacketType packet_type = PacketType.MESSAGE;
    private final Boolean encrypted;
    private final String message;
    private final String chat;

    @IgnoreValidation
    private final Long timestamp;


    private MessagePacket(String message, boolean encrypted, String chat ,long timestamp) {
        this.message = message;
        this.encrypted = encrypted;
        this.chat = chat;
        this.timestamp = timestamp;
    }

}
