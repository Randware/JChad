package net.jchad.shared.networking.packets.defaults;

import net.jchad.shared.networking.packets.PacketType;

public class ConnectionClosedPacket extends DefaultPacket {
    public ConnectionClosedPacket(String reason) {
        super(PacketType.CONNECTION_CLOSED, reason);
    }

}
