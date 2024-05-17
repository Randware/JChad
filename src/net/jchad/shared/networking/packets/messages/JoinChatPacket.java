package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public abstract class JoinChatPacket implements Packet {
    private final PacketType packet_type = PacketType.JOIN_CHAT;
    private final JoinChatPacket join_chat_type;

    public JoinChatPacket(JoinChatPacket join_chat_type) {
        this.join_chat_type = join_chat_type;
    }

    enum JoinChatPacketType {
        REQUEST,
        RESPONSE;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }
}
