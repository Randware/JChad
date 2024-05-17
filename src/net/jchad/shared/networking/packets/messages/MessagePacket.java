package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public sealed abstract class MessagePacket implements Packet permits ClientMessagePacket, ServerMessagePacket {

    private final PacketType packet_type;
    private final Boolean encrypted;
    private final String message;
    private final String chat;


    public MessagePacket(PacketType packet_type, String message, boolean encrypted, String chat) {
        this.packet_type = packet_type;
        this.message = message;
        this.encrypted = encrypted;
        this.chat = chat;


    }

    public PacketType getPacket_type() {
        return packet_type;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public String getMessage() {
        return message;
    }

    public String getChat() {
        return chat;
    }
}
