package net.jchad.shared.networking.packets;

public class PasswordRequestPacket extends PasswordPacket{
    public PasswordRequestPacket() {
        super(PasswordPacketType.PASSWORD_REQUEST);
    }
}
