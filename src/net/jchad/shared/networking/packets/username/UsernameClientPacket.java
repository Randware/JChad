package net.jchad.shared.networking.packets.username;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public class UsernameClientPacket implements Packet {

    private final PacketType packet_type = PacketType.USERNAME_CLIENT;
    private final String username;

    public UsernameClientPacket(String username) {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }
}
