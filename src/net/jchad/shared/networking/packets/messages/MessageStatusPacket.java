package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class MessageStatusPacket implements Packet {

    private final PacketType packet_type = PacketType.STATUS_MESSAGE;
    private final Status message_status;
    private final String reason;

    public MessageStatusPacket(Status message_status, String reason) {
        this.message_status = message_status;
        this.reason = reason;
    }

    public enum Status {
        FAILED,
        SUCCESS;
    }
}
