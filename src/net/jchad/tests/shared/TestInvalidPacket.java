package net.jchad.tests.shared;

import net.jchad.server.model.command.Command;
import net.jchad.shared.networking.packets.IgnoreValidation;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class TestInvalidPacket implements Packet {
    private final PacketType packet_type = PacketType.DISCOVERY;
    @IgnoreValidation
    private final Command command = null;
    private final int idk = 14;
}
