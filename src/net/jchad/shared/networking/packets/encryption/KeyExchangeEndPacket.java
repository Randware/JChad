package net.jchad.shared.networking.packets.encryption;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class KeyExchangeEndPacket implements Packet {
    private final PacketType packet_type = PacketType.KEY_EXCHANGE_END;
}
