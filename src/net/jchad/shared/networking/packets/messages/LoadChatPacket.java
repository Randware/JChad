package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public abstract class LoadChatPacket implements Packet {
    private final PacketType packet_type = PacketType.LOAD_CHAT;
    private final JoinChatPacketType join_chat_type;
    private final String chat_name;

    public LoadChatPacket(JoinChatPacketType join_chat_type, String chat_name) {
        this.join_chat_type = join_chat_type;
        this.chat_name = chat_name;
    }

    enum JoinChatPacketType {
        REQUEST,
        RESPONSE;
    }

    public PacketType getPacket_type() {
        return packet_type;
    }

    public String getChat_name() {
        return chat_name;
    }
}
