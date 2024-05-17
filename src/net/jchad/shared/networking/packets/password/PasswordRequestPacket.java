package net.jchad.shared.networking.packets.password;

public class PasswordRequestPacket extends PasswordPacket{
    public PasswordRequestPacket() {
        super(PasswordPacketType.PASSWORD_REQUEST);
    }
}
