package net.jchad.tests.shared;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class TestInvalidPacket implements Packet {
    private final PacketType packet_type = PacketType.DISCOVERY;
    private final int idk = 14;
}
