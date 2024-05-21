package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class MessageStatusFailedPacket implements Packet {
    private final PacketType packet_type = PacketType.STATUS_MESSAGE_FAILED;
    private final String reason;

    public MessageStatusFailedPacket(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
