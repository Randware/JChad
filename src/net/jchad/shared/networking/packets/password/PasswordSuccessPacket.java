package net.jchad.shared.networking.packets.password;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PasswordSuccessPacket implements Packet {
    private final PacketType packet_type = PacketType.PASSWORD_SUCCESS;
    public PasswordSuccessPacket() {}
}
