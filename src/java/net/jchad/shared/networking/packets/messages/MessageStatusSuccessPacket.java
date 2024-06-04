package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class MessageStatusSuccessPacket implements Packet {
    private final PacketType packet_type = PacketType.STATUS_MESSAGE_SUCCESS;

    public MessageStatusSuccessPacket() {
    }
}
