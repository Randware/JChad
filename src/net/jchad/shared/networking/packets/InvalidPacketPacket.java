package net.jchad.shared.networking.packets;

public class InvalidPacketPacket extends DefaultPacket{
    public InvalidPacketPacket(String message) {
        super(PacketType.INVALID, message == null ? "The received packet is invalid." : message);
    }
}
