package net.jchad.shared.packets;

public class BannedPacket extends DefaultPacket{
    public BannedPacket() {
        super(PacketType.BANNED, "YOU HAVE BEEN BANNED FROM THE SERVER!");
    }
}
