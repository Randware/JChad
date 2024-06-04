package net.jchad.shared.networking.packets.defaults;

import net.jchad.shared.networking.packets.PacketType;

public class BannedPacket extends DefaultPacket {
    public BannedPacket() {
        super(PacketType.BANNED, "YOU HAVE BEEN BANNED FROM THE SERVER!");
    }
    public BannedPacket(String reason) {super(PacketType.BANNED, "YOU HAVE BEEN BANNED FROM THE SERVER! REASON: " + reason);}
}
