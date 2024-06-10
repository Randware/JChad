package net.jchad.shared.networking.packets.password;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PasswordFailedPacket implements Packet {
    private final PacketType packet_type = PacketType.PASSWORD_FAILED;
    private final String error_message;

    public PasswordFailedPacket(String error_message) {
        this.error_message = error_message;
    }
}
