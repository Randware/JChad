package net.jchad.shared.networking.packets;

public class ConnectionClosedPacket extends DefaultPacket{
    public ConnectionClosedPacket(String reason) {
        super(PacketType.CONNECTION_CLOSED, reason);
    }

    public ConnectionClosedPacket() {
        this(null);
    }
}
