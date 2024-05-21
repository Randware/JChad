package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class JoinChatRequestPacket implements Packet {

    private final PacketType packet_type = PacketType.JOIN_CHAT_REQUEST;
    private final String chat_name;

    public JoinChatRequestPacket(String chat_name) {
        this.chat_name = chat_name;

    }


    public String getChat_name() {
        return chat_name;
    }
}
