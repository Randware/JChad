package net.jchad.shared.networking.packets.messages;

import net.jchad.server.model.chats.ChatMessage;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

import java.util.List;

public class JoinChatResponsePacket implements Packet {

    private final PacketType packet_type = PacketType.JOIN_CHAT_RESPONSE;
    private final String chat_name;
    public final List<ChatMessage> previous_messages;
    
    public JoinChatResponsePacket(String chat_name, List<ChatMessage> previous_messages) {
        this.chat_name = chat_name;
        this.previous_messages = previous_messages;
    }
}
