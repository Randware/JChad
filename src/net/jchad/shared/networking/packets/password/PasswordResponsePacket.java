package net.jchad.shared.networking.packets.password;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class PasswordResponsePacket implements Packet {
    private final PacketType packet_type = PacketType.PASSWORD_RESPONSE;
    private final String password;
    public PasswordResponsePacket(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
