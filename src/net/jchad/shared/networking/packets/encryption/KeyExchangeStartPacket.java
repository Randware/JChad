package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class KeyExchangeStartPacket implements Packet {
    private final PacketType packet_type = PacketType.KEY_EXCHANGE_START;

    public KeyExchangeStartPacket() {}
}
