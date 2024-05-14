package net.jchad.shared.networking.packets;

public class UsernamePacket implements Packet {

    private final PacketType packet_type = PacketType.USERNAME;

    private final String username;

    public UsernamePacket(String username) {
        this.username = username;
    }
}
