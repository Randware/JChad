package net.jchad.shared.networking.packets.username;

import net.jchad.shared.networking.packets.PacketType;

public class UsernameClientPacket extends  UsernamePacket {


    private final String username;

    public UsernameClientPacket(String username) {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }
}
