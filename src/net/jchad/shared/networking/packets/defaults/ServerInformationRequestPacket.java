package net.jchad.shared.networking.packets.defaults;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class ServerInformationRequestPacket implements Packet {
    private final PacketType packet_type = PacketType.SERVER_INFORMATION_REQUEST;
}
